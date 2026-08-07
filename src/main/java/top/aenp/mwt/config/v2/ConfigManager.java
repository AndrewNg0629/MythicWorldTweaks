package top.aenp.mwt.config.v2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.item.ItemEditor;
import top.aenp.mwt.misc.EnvironmentDetection;
import top.aenp.mwt.misc.WardenEntityStuff;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.payloads.NetworkSyncedConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class ConfigManager {
    public static final Codec<RegistryEntry<Item>> ITEM_ENTRY_CODEC = Registries.ITEM.getEntryCodec();
    private static final String configPathPrefix = System.getProperty("user.dir") + "/config/" + MythicWorldTweaks.MOD_ID;
    private static final String configFileName = "config.json";
    private static final String defaultConfigFileName = "default_config_v0.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private volatile static ConfigManager instance = new ConfigManager(ModConfig.DEFAULT_CONFIG);
    private static volatile boolean initialized = false;
    private final boolean modEnabled;
    private final boolean multiplayerSupportEnabled;
    private final ModConfig.ModIdValidationConfig modIdValidationConfig;
    private volatile ModConfig configFromFile;
    private volatile NetworkSyncedConfig configFromNetwork = null;
    private volatile ModConfig combinedConfig = null;

    public ConfigManager(ModConfig initialConfig) {
        modEnabled = initialConfig.modEnabled();
        multiplayerSupportEnabled = initialConfig.multiplayerSupportEnabled();
        modIdValidationConfig = initialConfig.modIdValidationConfig().enabled() ? initialConfig.modIdValidationConfig() : ModConfig.DEFAULT_CONFIG.modIdValidationConfig();
        configFromFile = initialConfig;
        combineConfig();
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    private static File ensureFile(String fileName) throws IOException {
        Path path = Paths.get(configPathPrefix, fileName);
        Files.createDirectories(path.getParent());
        if (Files.notExists(path)) {
            Files.createFile(path);
            try (FileWriter writer = new FileWriter(path.toFile())) {
                JsonElement jsonElement = ModConfig.CODEC.encodeStart(JsonOps.INSTANCE, ModConfig.DEFAULT_CONFIG).getOrThrow();
                writer.write(GSON.toJson(jsonElement));
                writer.flush();
            }
            MythicWorldTweaks.LOGGER.info("Created default config file {}", fileName);
        }
        return path.toFile();
    }

    private static DataResult<ModConfig> readConfigFromFile() {
        try (FileReader reader = new FileReader(ensureFile(configFileName))) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return ModConfig.CODEC.parse(JsonOps.INSTANCE, jsonElement);
        } catch (IOException e) {
            return DataResult.error(() -> String.format("IOException when reading config:\n%s", e));
        }
    }

    @SuppressWarnings({"unchecked", "BusyWait"})
    public static void initialize() {
        if (!initialized) {
            readConfigFromFile().ifSuccess(
                    config -> {
                        instance = new ConfigManager(config);
                        instance.applyBakedConfig();
                        MythicWorldTweaks.LOGGER.info("Successfully parsed initial config.");
                    }
            ).ifError(error -> MythicWorldTweaks.LOGGER.error("Failed to initialize config. See below for error message, correct your config, and restart minecraft.\n{}", error.message()));
            try {
                ensureFile(defaultConfigFileName);
            } catch (IOException e) {
                MythicWorldTweaks.LOGGER.error("Failed to place default config.", e);
            }
            Thread.startVirtualThread(() -> {
                Path configDir = Paths.get(configPathPrefix);
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    while (true) {
                        try {
                            WatchKey watchKey = watchService.take();
                            for (WatchEvent<?> event : watchKey.pollEvents()) {
                                WatchEvent.Kind<?> watchEventKind = event.kind();
                                if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    WatchEvent<Path> modifyEvent = (WatchEvent<Path>) event;
                                    if (modifyEvent.context().toString().startsWith("config.json")) {
                                        instance.updateConfigFromFile();
                                        Thread.sleep(5000L);
                                    }
                                }
                            }
                            if (!watchKey.reset()) {
                                throw new RuntimeException();
                            }
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    MythicWorldTweaks.LOGGER.error("Config file listener terminated unexpectedly.", e);
                }
                MythicWorldTweaks.LOGGER.info("config file listener done.");
            }).setName("Config File Listener");
            MythicWorldTweaks.LOGGER.info("Spun up config file listener.");
            initialized = true;
        } else {
            throw new IllegalStateException();
        }
    }

    public static ModConfig getConfig() {
        return instance.combinedConfig;
    }

    public void updateConfigFromFile() {
        readConfigFromFile().ifSuccess(result -> {
            if (!(result.modEnabled() == configFromFile.modEnabled() && result.multiplayerSupportEnabled() == configFromFile.multiplayerSupportEnabled() && Objects.equals(result.modIdValidationConfig(), configFromFile.modIdValidationConfig()))) {
                MythicWorldTweaks.LOGGER.warn("You edited immutable config, which can't be reloaded on-the-fly. Restart minecraft to change them.");
            }
            if (Objects.equals(result.tweaks(), configFromFile.tweaks()) && Objects.equals(result.itemEditorConfig(), configFromFile.itemEditorConfig())) {
                MythicWorldTweaks.LOGGER.info("Your mutable config didn't change.");
            } else {
                configFromFile = result;
                combineConfig();
                applyBakedConfig();
                MythicWorldTweaks.LOGGER.info("Your mutable config has been successfully updated.");
                MythicNetwork.INSTANCE.pushConfigDuringPlay();
            }
        }).ifError(error -> MythicWorldTweaks.LOGGER.error("Failed to parse config, config was not updated. See below for error message and correct your config.\n{}", error.message()));
    }

    public void onConfigPush(NetworkSyncedConfig syncedConfig) {
        if (EnvironmentDetection.isPhyClient) {
            configFromNetwork = syncedConfig;
            combineConfig();
            applyBakedConfig();
            MythicWorldTweaks.LOGGER.info("Applied config from the server.");
        }
    }

    public void exitMythicServerPlay() {
        configFromNetwork = null;
        combineConfig();
        applyBakedConfig();
    }

    private void combineConfig() {
        boolean localTweaksEnabled = configFromFile.tweaks().localTweaksEnabled();
        ModConfig.Tweaks.ValueTweaks valueTweaksFromFile = configFromFile.tweaks().valueTweaks();
        ModConfig.Tweaks.ValueTweaks.WardenAttributesControl wardenAttributesControlConfig = configFromNetwork != null ? configFromNetwork.wardenAttributesControl() : (localTweaksEnabled ? valueTweaksFromFile.wardenAttributesControl() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().wardenAttributesControl());
        ModConfig.Tweaks.ValueTweaks.VaultReuse vaultReuseConfig = configFromNetwork != null ? configFromNetwork.vaultReuse() : (localTweaksEnabled ? valueTweaksFromFile.vaultReuse() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().vaultReuse());
        ModConfig.ItemEditorConfig itemEditorConfig = configFromNetwork != null ? configFromNetwork.itemEditorConfig() : configFromFile.itemEditorConfig();
        combinedConfig = modEnabled ?
                new ModConfig(
                        true,
                        multiplayerSupportEnabled,
                        modIdValidationConfig,
                        new ModConfig.Tweaks(
                                localTweaksEnabled,
                                localTweaksEnabled ? configFromFile.tweaks().localToggleTweaks1() : ModConfig.DEFAULT_CONFIG.tweaks().localToggleTweaks1(),
                                configFromNetwork != null ? configFromNetwork.syncedToggleTweaks1() : (localTweaksEnabled ? configFromFile.tweaks().syncedToggleTweaks1() : ModConfig.DEFAULT_CONFIG.tweaks().syncedToggleTweaks1()),
                                new ModConfig.Tweaks.ValueTweaks(
                                        localTweaksEnabled && valueTweaksFromFile.fireballAutoDiscarding().enabled() ? valueTweaksFromFile.fireballAutoDiscarding() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().fireballAutoDiscarding(),
                                        localTweaksEnabled && valueTweaksFromFile.stuffedShulkerBoxStacking().enabled() ? valueTweaksFromFile.stuffedShulkerBoxStacking() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().stuffedShulkerBoxStacking(),
                                        localTweaksEnabled && valueTweaksFromFile.shulkerBoxNesting().enabled() ? valueTweaksFromFile.shulkerBoxNesting() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().shulkerBoxNesting(),
                                        wardenAttributesControlConfig.enabled() ? wardenAttributesControlConfig : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().wardenAttributesControl(),
                                        localTweaksEnabled && valueTweaksFromFile.wardenSonicBoomControl().enabled() ? valueTweaksFromFile.wardenSonicBoomControl() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().wardenSonicBoomControl(),
                                        localTweaksEnabled && valueTweaksFromFile.playerDeathItemProtection().enabled() ? valueTweaksFromFile.playerDeathItemProtection() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().playerDeathItemProtection(),
                                        vaultReuseConfig
                                )
                        ),
                        itemEditorConfig.enabled() ? itemEditorConfig : ModConfig.DEFAULT_CONFIG.itemEditorConfig(),
                        configFromFile.configVersion()
                )
                : ModConfig.DEFAULT_CONFIG;
    }

    private void applyBakedConfig() {
        ItemEditor.applyFromModConfig();
        WardenEntityStuff.modifyWardenAttributes();
        WardenEntityStuff.WardenEntityTrack.refreshWardens();
    }
}
