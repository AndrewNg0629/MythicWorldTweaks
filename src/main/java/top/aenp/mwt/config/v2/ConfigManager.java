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
    public static final int CONFIG_VERSION = 2;
    public static final Codec<RegistryEntry<Item>> ITEM_ENTRY_CODEC = Registries.ITEM.getEntryCodec();
    private static final String CONFIG_PATH_PREFIX = System.getProperty("user.dir") + "/config/" + MythicWorldTweaks.MOD_ID;
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String DEFAULT_CONFIG_FILE_NAME = String.format("default_config_v%s.json", CONFIG_VERSION);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private volatile static ConfigManager instance = new ConfigManager(ModConfig.DEFAULT_CONFIG);
    private static volatile boolean initialized = false;
    private final boolean modEnabled;
    private final boolean multiplayerSupportEnabled;
    private final ModConfig.ModIdValidationConfig modIdValidationConfig;
    private volatile ModConfig configFromFile;
    private volatile NetworkSyncedConfig configFromNetwork = null;
    private volatile NetworkSyncedConfig configToSend = null;
    private volatile ModConfig combinedConfig = null;
    private boolean initialConfigSucceeded = false;

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
        Path path = Paths.get(CONFIG_PATH_PREFIX, fileName);
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
        try (FileReader reader = new FileReader(ensureFile(CONFIG_FILE_NAME))) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            int fileConfigVersion = jsonElement.getAsJsonObject().get("config_version").getAsInt();
            if (fileConfigVersion == CONFIG_VERSION) {
                return ModConfig.CODEC.parse(JsonOps.INSTANCE, jsonElement);
            } else {
                return DataResult.error(() -> String.format("The config version of your file is %s, while %s is expected! Migrate your config according to the generated default config %s.", fileConfigVersion, CONFIG_VERSION, DEFAULT_CONFIG_FILE_NAME));
            }
        } catch (Exception e) {
            return DataResult.error(() -> String.format("Exception occurred when reading config from file:\n%s", e));
        }
    }

    public static void initialize() {
        if (!initialized) {
            readConfigFromFile().ifSuccess(
                    config -> {
                        instance = new ConfigManager(config);
                        instance.applyBakedConfig();
                        instance.initialConfigSucceeded = true;
                        MythicWorldTweaks.LOGGER.info("Successfully parsed initial config.");
                    }
            ).ifError(error -> MythicWorldTweaks.LOGGER.error("Failed to initialize config. See below for error message, correct your config, and restart minecraft.\n{}", error.message()));
            try {
                ensureFile(DEFAULT_CONFIG_FILE_NAME);
            } catch (IOException e) {
                MythicWorldTweaks.LOGGER.error("Failed to place default config.", e);
            }
            if (instance.initialConfigSucceeded) {
                startListener();
            }
            initialized = true;
        } else {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings({"unchecked"})
    private static void startListener() {
        Thread.startVirtualThread(() -> {
            Path configDir = Paths.get(CONFIG_PATH_PREFIX);
            long lastTriggered = 0L;
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    try {
                        WatchKey watchKey = watchService.take();
                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            WatchEvent.Kind<?> watchEventKind = event.kind();
                            if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                WatchEvent<Path> modifyEvent = (WatchEvent<Path>) event;
                                if (modifyEvent.context().toString().startsWith("config.json") && System.currentTimeMillis() >= lastTriggered + 3000L) {
                                    lastTriggered = System.currentTimeMillis();
                                    instance.updateConfigFromFile();
                                }
                            }
                        }
                        if (!watchKey.reset()) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (Exception e) {
                MythicWorldTweaks.LOGGER.error("Config file listener terminated unexpectedly.", e);
            }
        }).setName("Config File Listener");
        MythicWorldTweaks.LOGGER.info("Started config file listener.");
    }

    public static ModConfig getConfig() {
        return instance.combinedConfig;
    }

    public NetworkSyncedConfig getConfigToSend() {
        return configToSend;
    }

    public void updateConfigFromFile() {
        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                MythicWorldTweaks.LOGGER.info("Exception updating config.", e);
                return;
            }
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
                    if (ConfigManager.getConfig().multiplayerSupportEnabled()) {
                        MythicNetwork.INSTANCE.pushConfigDuringPlay();
                    }
                }
            }).ifError(error -> MythicWorldTweaks.LOGGER.error("Failed to parse config, config was not updated. See below for error message and correct your config.\n{}", error.message()));
        }).setName("Config File Updater");
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
        ModConfig.Tweaks.ValueTweaks defaultValueTweaks = ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks();
        ModConfig.Tweaks.ValueTweaks.WardenAttributesControl wardenAttributesControlConfig = configFromNetwork != null ? configFromNetwork.wardenAttributesControl() : (localTweaksEnabled ? valueTweaksFromFile.wardenAttributesControl() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().wardenAttributesControl());
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
                                        localTweaksEnabled && valueTweaksFromFile.fireballAutoDiscarding().enabled() ? valueTweaksFromFile.fireballAutoDiscarding() : defaultValueTweaks.fireballAutoDiscarding(),
                                        localTweaksEnabled && valueTweaksFromFile.stuffedShulkerBoxStacking().enabled() ? valueTweaksFromFile.stuffedShulkerBoxStacking() : defaultValueTweaks.stuffedShulkerBoxStacking(),
                                        localTweaksEnabled && valueTweaksFromFile.shulkerBoxNesting().enabled() ? valueTweaksFromFile.shulkerBoxNesting() : defaultValueTweaks.shulkerBoxNesting(),
                                        wardenAttributesControlConfig.enabled() ? wardenAttributesControlConfig : defaultValueTweaks.wardenAttributesControl(),
                                        localTweaksEnabled && valueTweaksFromFile.wardenSonicBoomControl().enabled() ? valueTweaksFromFile.wardenSonicBoomControl() : defaultValueTweaks.wardenSonicBoomControl(),
                                        localTweaksEnabled && valueTweaksFromFile.playerDeathItemProtection().enabled() ? valueTweaksFromFile.playerDeathItemProtection() : defaultValueTweaks.playerDeathItemProtection(),
                                        localTweaksEnabled ? valueTweaksFromFile.vaultReuse() : ModConfig.DEFAULT_CONFIG.tweaks().valueTweaks().vaultReuse(),
                                        localTweaksEnabled && valueTweaksFromFile.itemExplosionResistance().enabled() ? valueTweaksFromFile.itemExplosionResistance() : defaultValueTweaks.itemExplosionResistance(),
                                        localTweaksEnabled && valueTweaksFromFile.trialSpawnerCooldownOverride().enabled() ? valueTweaksFromFile.trialSpawnerCooldownOverride() : defaultValueTweaks.trialSpawnerCooldownOverride()
                                )
                        ),
                        itemEditorConfig.enabled() ? itemEditorConfig : ModConfig.DEFAULT_CONFIG.itemEditorConfig(),
                        configFromFile.configVersion()
                )
                : ModConfig.DEFAULT_CONFIG;
        configToSend = new NetworkSyncedConfig(combinedConfig.tweaks().syncedToggleTweaks1(), combinedConfig.tweaks().valueTweaks().wardenAttributesControl(), combinedConfig.itemEditorConfig());
    }

    private void applyBakedConfig() {
        ItemEditor.applyFromModConfig();
        WardenEntityStuff.modifyWardenAttributes();
        WardenEntityStuff.WardenEntityTracker.INSTANCE.refreshWardens();
    }
}
