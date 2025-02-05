package online.andrew2007.mythic.config.configFileParser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Set;

public record BinaryToggleTweaksConfig(
        boolean throwableFireCharge,
        boolean largeFireCharge,
        boolean editablePlayerData,
        boolean blocksFriendlyCreepers,
        boolean itemExplosionResistance,
        boolean nestableShulkerBoxes,
        boolean rideablePlayers,
        boolean playerRidingGestures,
        boolean playerRidingProtection,
        boolean dispensableTridents,
        boolean voidReturnableTrident,
        boolean multiTridentDamage,
        boolean persistentTridents,
        boolean silentWardens,
        boolean constantVillagerConversion,
        boolean fakePlayerSleepExclusion,
        boolean sleepingExtras
) {
    public static class Deserializer implements CustomJsonDeserializer<BinaryToggleTweaksConfig> {

        @Override
        public BinaryToggleTweaksConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            if (!jsonObject.keySet().equals(Set.of(
                    "throwable_fire_charge",
                    "large_fire_charge",
                    "editable_player_data",
                    "blocks_friendly_creepers",
                    "item_explosion_resistance",
                    "nestable_shulker_boxes",
                    "rideable_players",
                    "player_riding_gestures",
                    "player_riding_protection",
                    "dispensable_tridents",
                    "void_returnable_trident",
                    "multi_trident_damage",
                    "persistent_tridents",
                    "silent_wardens",
                    "constant_villager_conversion",
                    "fake_player_sleep_exclusion",
                    "sleeping_extras"
            ))) {
                throw new JsonParseException("Wrong config structure, please have a check.");
            }
            return new BinaryToggleTweaksConfig(
                    readBoolean(jsonObject.get("throwable_fire_charge")),
                    readBoolean(jsonObject.get("large_fire_charge")),
                    readBoolean(jsonObject.get("editable_player_data")),
                    readBoolean(jsonObject.get("blocks_friendly_creepers")),
                    readBoolean(jsonObject.get("item_explosion_resistance")),
                    readBoolean(jsonObject.get("nestable_shulker_boxes")),
                    readBoolean(jsonObject.get("rideable_players")),
                    readBoolean(jsonObject.get("player_riding_gestures")),
                    readBoolean(jsonObject.get("player_riding_protection")),
                    readBoolean(jsonObject.get("dispensable_tridents")),
                    readBoolean(jsonObject.get("void_returnable_trident")),
                    readBoolean(jsonObject.get("multi_trident_damage")),
                    readBoolean(jsonObject.get("persistent_tridents")),
                    readBoolean(jsonObject.get("silent_wardens")),
                    readBoolean(jsonObject.get("constant_villager_conversion")),
                    readBoolean(jsonObject.get("fake_player_sleep_exclusion")),
                    readBoolean(jsonObject.get("sleeping_extras"))
            );
        }
    }
}
