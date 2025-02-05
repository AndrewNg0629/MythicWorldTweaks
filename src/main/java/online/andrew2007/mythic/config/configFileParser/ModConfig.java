package online.andrew2007.mythic.config.configFileParser;

import com.google.gson.*;
import online.andrew2007.mythic.config.ConfigLoader;

import java.lang.reflect.Type;
import java.util.Set;

public record ModConfig(
        boolean modEnabled,
        boolean modDataPackEnabled,
        boolean tweaksEnabled,
        boolean serverPlaySupportEnabled,
        ModIdValidationConfig modIdValidationConfig,
        BinaryToggleTweaksConfig binaryToggleTweaksConfig,
        ParamsRequiredTweaksConfig paramsRequiredTweaksConfig,
        ItemEditorConfig itemEditorConfig
) {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ModConfig.class, new ModConfig.Deserializer())
            .registerTypeAdapter(ModIdValidationConfig.class, new ModIdValidationConfig.Deserializer())
            .registerTypeAdapter(BinaryToggleTweaksConfig.class, new BinaryToggleTweaksConfig.Deserializer())
            .registerTypeAdapter(ParamsRequiredTweaksConfig.class, new ParamsRequiredTweaksConfig.Deserializer())
            .registerTypeAdapter(ParamsRequiredTweaksConfig.AutoDiscardingFireBallConfig.class, new ParamsRequiredTweaksConfig.AutoDiscardingFireBallConfig.Deserializer())
            .registerTypeAdapter(ParamsRequiredTweaksConfig.StuffedShulkerBoxStackLimitConfig.class, new ParamsRequiredTweaksConfig.StuffedShulkerBoxStackLimitConfig.Deserializer())
            .registerTypeAdapter(ParamsRequiredTweaksConfig.ShulkerBoxNestingLimitConfig.class, new ParamsRequiredTweaksConfig.ShulkerBoxNestingLimitConfig.Deserializer())
            .registerTypeAdapter(ParamsRequiredTweaksConfig.WardenAttributesWeakeningConfig.class, new ParamsRequiredTweaksConfig.WardenAttributesWeakeningConfig.Deserializer())
            .registerTypeAdapter(ParamsRequiredTweaksConfig.WardenSonicBoomWeakeningConfig.class, new ParamsRequiredTweaksConfig.WardenSonicBoomWeakeningConfig.Deserializer())
            .registerTypeAdapter(ItemEditorConfig.class, new ItemEditorConfig.Deserializer())
            .registerTypeAdapter(ItemEditorConfig.ItemEditorConfigUnit.class, new ItemEditorConfig.ItemEditorConfigUnit.Deserializer())
            .registerTypeAdapter(ItemEditorConfig.FoodProperty.class, new ItemEditorConfig.FoodProperty.Deserializer())
            .registerTypeAdapter(ItemEditorConfig.FoodStatusEffectUnit.class, new ItemEditorConfig.FoodStatusEffectUnit.Deserializer())
            .create();
    public static class Deserializer implements CustomJsonDeserializer<ModConfig> {
        @Override
        public ModConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            if (!jsonObject.keySet().equals(Set.of(
                    "mod_enabled",
                    "mod_data_pack_enabled",
                    "tweaks_enabled",
                    "server_play_support_enabled",
                    "mod_id_validation",
                    "binary_toggle_tweaks",
                    "params_required_tweaks",
                    "item_editor"
            ))) {
                throw new JsonParseException("Wrong config structure, please have a check.");
            }
            ModIdValidationConfig modIdValidationConfig = context.deserialize(jsonObject.get("mod_id_validation"), ModIdValidationConfig.class);
            BinaryToggleTweaksConfig binaryToggleTweaksConfig = ConfigLoader.isItemEditorParserReady() ? context.deserialize(jsonObject.get("binary_toggle_tweaks"), BinaryToggleTweaksConfig.class) : null;
            ParamsRequiredTweaksConfig paramsRequiredTweaksConfig = ConfigLoader.isItemEditorParserReady() ? context.deserialize(jsonObject.get("params_required_tweaks"), ParamsRequiredTweaksConfig.class) : null;
            ItemEditorConfig itemEditorConfig = ConfigLoader.isItemEditorParserReady() ? context.deserialize(jsonObject.get("item_editor"), ItemEditorConfig.class) : null;
            return new ModConfig(
                    readBoolean(jsonObject.get("mod_enabled")),
                    readBoolean(jsonObject.get("mod_data_pack_enabled")),
                    readBoolean(jsonObject.get("tweaks_enabled")),
                    readBoolean(jsonObject.get("server_play_support_enabled")),
                    modIdValidationConfig,
                    binaryToggleTweaksConfig,
                    paramsRequiredTweaksConfig,
                    itemEditorConfig
            );
        }
    }
}
