package online.andrew2007.mythic.config;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.configFileParser.ModConfig;
import online.andrew2007.mythic.util.EnvironmentDetection;
import online.andrew2007.mythic.util.LocalToaster;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class ConfigLoader {
    private static boolean initState = false;
    private static ImmutableBiMap<Identifier, Item> allItems;
    private static ImmutableBiMap<Identifier, RegistryEntry<StatusEffect>> allStatusEffects;
    private static ImmutableList<String> allModIds;
    private static boolean isItemEditorParserReady = false;
    private static MinecraftServer currentServer = null;
    private static boolean isDuringServerRuntime = false;
    private static ModConfig currentModConfig = null;
    private static final Timer notifier = new Timer();
    private static boolean notifierRunning = false;
    private static TimerTask notifierTask = null;

    public static void onServerStarting(MinecraftServer server) {
        tryConfigSystemInit();
        currentServer = server;
        isDuringServerRuntime = true;
        RuntimeController.loadTransmittableParamsFromConfig();
    }

    public static void onServerStopping() {
        currentServer = null;
        isDuringServerRuntime = false;
        if (!EnvironmentDetection.isPhyClient) {
            ConfigFileListener.stopListener();
        }
    }

    public static void tryConfigSystemInit() throws RuntimeException {
        if (!initState) {
            MythicWorldTweaks.LOGGER.info("MythicWorldTweaks' config system starts to be initialized!");
            initState = true;
            ImmutableBiMap.Builder<Identifier, Item> itemMapBuilder = ImmutableBiMap.builder();
            for (Item item : Registries.ITEM) {
                itemMapBuilder.put(Registries.ITEM.getId(item), item);
            }
            allItems = itemMapBuilder.build();
            ImmutableBiMap.Builder<Identifier, RegistryEntry<StatusEffect>> effectMapBuilder = ImmutableBiMap.builder();
            for (StatusEffect statusEffect : Registries.STATUS_EFFECT) {
                effectMapBuilder.put(Objects.requireNonNull(Registries.STATUS_EFFECT.getId(statusEffect), "Error initializing config system!"),
                        Registries.STATUS_EFFECT.getEntry(statusEffect)
                );
            }
            allStatusEffects = effectMapBuilder.build();
            isItemEditorParserReady = true;
            ImmutableList.Builder<String> modIdListBuilder = ImmutableList.builder();
            FabricLoader fabricLoader = FabricLoader.getInstance();
            fabricLoader.getAllMods().forEach(modContainer -> modIdListBuilder.add(modContainer.getMetadata().getId()));
            allModIds = modIdListBuilder.build();
            try {
                tryPlaceDefaultConfigFile(true);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create default config file.");
            }
            try {
                currentModConfig = getConfigObject();
            } catch (RuntimeException e) {
                MythicWorldTweaks.LOGGER.error("Unable to load config on startup, Minecraft is shutting down.");
                throw e;
            }
            if (RuntimeController.getLocalRuntimeParams().modEnabled()) {
                ConfigFileListener.initiateListener();
            }
        }
    }

    public static void onConfigHotUpdate() {
        if (notifierRunning) {
            notifierTask.cancel();
            notifierRunning = false;
            notifierTask = null;
        }
        try {
            currentModConfig = getConfigObject();
            MythicWorldTweaks.LOGGER.info("Successfully parsed hot updating config.");
        } catch (RuntimeException e) {
            MythicWorldTweaks.LOGGER.error("Failed to parse hot updating config, current config won't change, please try editing it again.", e);
            if (EnvironmentDetection.isPhyClient && !notifierRunning) {
                notifierTask = new TimerTask() {
                    @Override
                    public void run() {
                        LocalToaster.toast(Text.of("MythicWorldTweaks"), Text.translatable("mythicworldtweaks.config.fail_to_hot_update"));
                    }
                };
                notifier.schedule(notifierTask, 0, 20000);
                notifierRunning = true;
            }
            return;
        }
        if (ConfigLoader.isDuringServerRuntime()) {
            RuntimeController.loadTransmittableParamsFromConfig();
            MythicWorldTweaks.LOGGER.info("Server is running, config loaded immediately.");
            LocalToaster.toast(Text.of("MythicWorldTweaks"), Text.translatable("mythicworldtweaks.config.hot_update_succeed"));
        } else {
            LocalToaster.toast(Text.of("MythicWorldTweaks"), Text.translatable("mythicworldtweaks.config.hot_update_later"));
        }
    }

    @NotNull
    public static ImmutableBiMap<Identifier, Item> getAllItems() throws IllegalStateException {
        if (!initState) {
            throw new IllegalStateException("The config system hasn't been initialized, allItems is empty.");
        }
        return allItems;
    }

    @NotNull
    public static ImmutableBiMap<Identifier, RegistryEntry<StatusEffect>> getAllStatusEffects() throws IllegalStateException {
        if (!initState) {
            throw new IllegalStateException("The config system hasn't been initialized, allStatusEffects is empty.");
        }
        return allStatusEffects;
    }

    @NotNull
    public static ImmutableList<String> getAllModIds() throws IllegalStateException {
        if (!initState) {
            throw new IllegalStateException("The config system hasn't been initialized, allStatusEffects is empty.");
        }
        return allModIds;
    }

    public static ModConfig getCurrentModConfig() {
        return currentModConfig;
    }

    public static ModConfig getConfigObject() throws RuntimeException {
        try {
            String configContent = readConfigContent();
            return ModConfig.GSON.fromJson(configContent, ModConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get config object.", e);
        }
    }

    private static String readConfigContent() throws IOException {
        String configFilePath = System.getProperty("user.dir") + "/config/" + MythicWorldTweaks.MOD_ID + "/config.json";
        File configFile = new File(configFilePath);
        tryPlaceDefaultConfigFile(false);
        try (FileReader fileReader = new FileReader(configFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            int byteCount;
            char[] charBuffer = new char[128];
            StringBuilder fileIndex = new StringBuilder();
            while ((byteCount = bufferedReader.read(charBuffer)) != -1) {
                fileIndex.append(new String(charBuffer, 0, byteCount));
            }
            return fileIndex.toString();
        }
    }

    private static String getDefaultConfig() throws IOException, NullPointerException {
        ClassLoader classLoader = ConfigLoader.class.getClassLoader();
        try (InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream("config/default_config.json"));
             InputStreamReader reader = new InputStreamReader(inputStream);
        ) {
            int byteCount;
            char[] charBuffer = new char[128];
            StringBuilder fileIndex = new StringBuilder();
            while ((byteCount = reader.read(charBuffer)) != -1) {
                fileIndex.append(new String(charBuffer, 0, byteCount));
            }
            return fileIndex.toString();
        }
    }

    private static void tryPlaceDefaultConfigFile(boolean isDemo) throws IOException {
        String defaultConfigFilePath = System.getProperty("user.dir") + "/config/" + MythicWorldTweaks.MOD_ID + (isDemo ? "/default_config.json" : "/config.json");
        File defaultConfigFile = new File(defaultConfigFilePath);
        if (!defaultConfigFile.exists()) {
            MythicWorldTweaks.LOGGER.info("MythicWorldTweaks' {}config file doesn't exist, creating new one.", isDemo ? "default " : "");
            defaultConfigFile.getParentFile().mkdirs();
            defaultConfigFile.createNewFile();
            String defaultConfigContent = getDefaultConfig();
            try (FileWriter fileWriter = new FileWriter(defaultConfigFile, false)) {
                fileWriter.write(defaultConfigContent);
                fileWriter.flush();
            }
        }
    }

    public static MinecraftServer getCurrentServer() {
        if (!isDuringServerRuntime) {
            throw new IllegalStateException("Server isn't running, unable to get!");
        }
        return currentServer;
    }

    public static boolean isDuringServerRuntime() {
        return isDuringServerRuntime;
    }

    public static boolean isItemEditorParserReady() {
        return isItemEditorParserReady;
    }
}
