package online.andrew2007.mythic.config;

import com.google.common.collect.ImmutableBiMap;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.configFileParser.ModConfig;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;


public class ConfigLoader {

    private static boolean initState = false;
    private static ImmutableBiMap<Identifier, Item> allItems;
    private static ImmutableBiMap<Identifier, RegistryEntry<StatusEffect>> allStatusEffects;
    private static MinecraftServer currentServer = null;
    private static boolean isDuringServerRuntime = false;

    public static void onServerStarting(MinecraftServer server) {

        currentServer = server;
        isDuringServerRuntime = true;
        tryConfigSystemInit();
    }

    public static void onServerStopping() {
        currentServer = null;
        isDuringServerRuntime = false;
    }

    public static void tryConfigSystemInit() {
        if (!initState) {
            MythicWorldTweaks.LOGGER.info("Config system initialization starts!");
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
        }
        try {
            String s1 = readFile(new File(System.getProperty("user.dir") + "\\..\\src\\main\\resources\\testConfig\\config_proto.json"));
            ModConfig s2 = ModConfig.GSON.fromJson(s1, ModConfig.class);
            RuntimeController.loadTransmittableParamsFromConfig(s2);
        } catch (IOException e) {
            MythicWorldTweaks.LOGGER.error("Whoops!", e);
        }
        String s1 = TransmittableRuntimeParams.GSON.toJson(Items.DIAMOND);
        MythicWorldTweaks.LOGGER.info(s1);
        Item s2 = TransmittableRuntimeParams.GSON.fromJson(s1, Item.class);
        MythicWorldTweaks.LOGGER.info(s2.toString());

        String s3 = TransmittableRuntimeParams.GSON.toJson(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, -1, 0));
        MythicWorldTweaks.LOGGER.info(s3);
        StatusEffectInstance s4 = TransmittableRuntimeParams.GSON.fromJson(s3, StatusEffectInstance.class);
        MythicWorldTweaks.LOGGER.info(s4.toString());

        FoodComponent test = new FoodComponent.Builder()
                .nutrition(4)
                .saturationModifier(1.2F)
                .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0F)
                .alwaysEdible()
                .usingConvertsTo(Items.APPLE)
                .build();

        String s5 = TransmittableRuntimeParams.GSON.toJson(test);
        MythicWorldTweaks.LOGGER.info(s5);
        FoodComponent s6 = TransmittableRuntimeParams.GSON.fromJson(s5, FoodComponent.class);
        MythicWorldTweaks.LOGGER.info(s6.toString());

        String s7 = TransmittableRuntimeParams.GSON.toJson(FoodComponents.APPLE);
        MythicWorldTweaks.LOGGER.info(s7);
        FoodComponent s8 = TransmittableRuntimeParams.GSON.fromJson(s7, FoodComponent.class);
        MythicWorldTweaks.LOGGER.info(s8.toString());

        String s9 = TransmittableRuntimeParams.GSON.toJson(TransmittableRuntimeParams.getDefaultInstance());
        MythicWorldTweaks.LOGGER.info(s9);
        TransmittableRuntimeParams s10 = TransmittableRuntimeParams.GSON.fromJson(s9, TransmittableRuntimeParams.class);
        MythicWorldTweaks.LOGGER.info(s10.toString());

        //System.exit(0);
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

    public static String readFile(File fileObject) throws IOException {
        FileReader fileReader = new FileReader(fileObject);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int byteCount;
        char[] charBuffer = new char[128];
        StringBuilder fileIndex = new StringBuilder();
        while ((byteCount = bufferedReader.read(charBuffer)) != -1) {
            fileIndex.append(new String(charBuffer, 0, byteCount));
        }
        bufferedReader.close();
        fileReader.close();
        return fileIndex.toString();
    }
}
