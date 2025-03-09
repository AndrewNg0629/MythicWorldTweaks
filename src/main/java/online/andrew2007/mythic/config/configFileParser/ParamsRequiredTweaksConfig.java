package online.andrew2007.mythic.config.configFileParser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Set;

public record ParamsRequiredTweaksConfig(
        AutoDiscardingFireBallConfig autoDiscardingFireBallConfig,
        StuffedShulkerBoxStackLimitConfig stuffedShulkerBoxStackLimitConfig,
        ShulkerBoxNestingLimitConfig shulkerBoxNestingLimitConfig,
        WardenAttributesWeakeningConfig wardenAttributesWeakeningConfig,
        WardenSonicBoomWeakeningConfig wardenSonicBoomWeakeningConfig
) {
    public record AutoDiscardingFireBallConfig(boolean enabled, int maxLifeTicks) {
        public static class Deserializer implements CustomJsonDeserializer<AutoDiscardingFireBallConfig> {
            @Override
            public AutoDiscardingFireBallConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                checkKeys(Set.of("enabled", "max_life_ticks"), jsonObject.keySet(), true);
                int maxLifeTicks = jsonObject.get("max_life_ticks").getAsInt();
                if (maxLifeTicks <= 0) {
                    throw new JsonParseException("Wrong config value for \"max_life_time\", positive value is required.");
                }
                return new AutoDiscardingFireBallConfig(readBoolean(jsonObject.get("enabled")), maxLifeTicks);
            }
        }
    }

    public record StuffedShulkerBoxStackLimitConfig(boolean enabled, int maxStackSize) {
        public static class Deserializer implements CustomJsonDeserializer<StuffedShulkerBoxStackLimitConfig> {
            @Override
            public StuffedShulkerBoxStackLimitConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                checkKeys(Set.of("enabled", "max_stack_size"), jsonObject.keySet(), true);
                int maxStackSize = jsonObject.get("max_stack_size").getAsInt();
                if (maxStackSize <= 0) {
                    throw new JsonParseException("Wrong config value for \"max_stack_size\", positive value is required.");
                }
                return new StuffedShulkerBoxStackLimitConfig(readBoolean(jsonObject.get("enabled")), maxStackSize);
            }
        }
    }

    public record ShulkerBoxNestingLimitConfig(boolean enabled, int maxLayers) {
        public static class Deserializer implements CustomJsonDeserializer<ShulkerBoxNestingLimitConfig> {
            @Override
            public ShulkerBoxNestingLimitConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                checkKeys(Set.of("enabled", "max_layers"), jsonObject.keySet(), true);
                int maxLayers = jsonObject.get("max_layers").getAsInt();
                if (maxLayers <= 0 || maxLayers > 10) {
                    throw new JsonParseException("Wrong config value for \"max_layers\", value between 0 and 10 is required.");
                }
                return new ShulkerBoxNestingLimitConfig(readBoolean(jsonObject.get("enabled")), maxLayers);
            }
        }
    }

    public record WardenAttributesWeakeningConfig(
            boolean enabled,
            double maxHealth,
            double knockBackResistance,
            double meleeAttackDamage,
            double meleeAttackKnockBack,
            double idleMovementSpeed,
            float chasingMovementSpeed,
            int attackIntervalTicks
    ) {
        public static class Deserializer implements CustomJsonDeserializer<WardenAttributesWeakeningConfig> {
            @Override
            public WardenAttributesWeakeningConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                checkKeys(Set.of(
                                "enabled",
                                "max_health",
                                "knock_back_resistance",
                                "melee_attack_damage",
                                "melee_attack_knock_back",
                                "idle_movement_speed",
                                "chasing_movement_speed",
                                "attack_interval_ticks"
                        ),
                        jsonObject.keySet(),
                        true
                );
                double maxHealth = jsonObject.get("max_health").getAsDouble();
                double knockBackResistance = jsonObject.get("knock_back_resistance").getAsDouble();
                double meleeAttackDamage = jsonObject.get("melee_attack_damage").getAsDouble();
                double meleeAttackKnockBack = jsonObject.get("melee_attack_knock_back").getAsDouble();
                double idleMovementSpeed = jsonObject.get("idle_movement_speed").getAsDouble();
                float chasingMovementSpeed = jsonObject.get("chasing_movement_speed").getAsFloat();
                int attackIntervalTicks = jsonObject.get("attack_interval_ticks").getAsInt();
                if (maxHealth <= 0) {
                    throw new JsonParseException("Wrong config value for \"max_health\", positive value is required.");
                } else if (knockBackResistance < 0D || knockBackResistance > 1D) {
                    throw new JsonParseException("Wrong config value for \"knock_back_resistance\", value from 0.0 to 1.0 is required.");
                } else if (meleeAttackDamage < 0) {
                    throw new JsonParseException("Wrong config value for \"melee_attack_damage\", non-negative value is required.");
                } else if (meleeAttackKnockBack < 0) {
                    throw new JsonParseException("Wrong config value for \"melee_attack_knock_back\", non-negative value is required.");
                } else if (idleMovementSpeed <= 0) {
                    throw new JsonParseException("Wrong config value for \"idle_movement_speed\", positive value is required.");
                } else if (chasingMovementSpeed <= 0) {
                    throw new JsonParseException("Wrong config value for \"chasing_movement_speed\", positive value is required.");
                } else if (attackIntervalTicks <= 0) {
                    throw new JsonParseException("Wrong config value for \"attack_interval_ticks\", positive value is required.");
                }
                return new WardenAttributesWeakeningConfig(
                        readBoolean(jsonObject.get("enabled")),
                        maxHealth,
                        knockBackResistance,
                        meleeAttackDamage,
                        meleeAttackKnockBack,
                        idleMovementSpeed,
                        chasingMovementSpeed,
                        attackIntervalTicks);
            }
        }
    }

    public record WardenSonicBoomWeakeningConfig(
            boolean enabled,
            float sonicBoomDamage,
            double sonicBoomKnockBackRate,
            int sonicBoomIntervalTicks
    ) {
        public static class Deserializer implements CustomJsonDeserializer<WardenSonicBoomWeakeningConfig> {
            @Override
            public WardenSonicBoomWeakeningConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                checkKeys(Set.of(
                                "enabled",
                                "sonic_boom_damage",
                                "sonic_boom_knock_back_rate",
                                "sonic_boom_interval_ticks"
                        ),
                        jsonObject.keySet(),
                        true
                );
                float sonicBoomDamage = jsonObject.get("sonic_boom_damage").getAsFloat();
                double sonicBoomKnockBackRate = jsonObject.get("sonic_boom_knock_back_rate").getAsDouble();
                int sonicBoomIntervalTicks = jsonObject.get("sonic_boom_interval_ticks").getAsInt();
                if (sonicBoomDamage < 0) {
                    throw new JsonParseException("Wrong config value for \"sonic_boom_damage\", non-negative value is required.");
                } else if (sonicBoomKnockBackRate < 0D) {
                    throw new JsonParseException("Wrong config value for \"sonic_boom_knock_back_rate\", non-negative value is required.");
                } else if (sonicBoomIntervalTicks <= 0) {
                    throw new JsonParseException("Wrong config value for \"sonic_boom_interval_ticks\", positive value is required.");
                }
                return new WardenSonicBoomWeakeningConfig(
                        readBoolean(jsonObject.get("enabled")),
                        sonicBoomDamage,
                        sonicBoomKnockBackRate,
                        sonicBoomIntervalTicks
                );
            }
        }
    }

    public static class Deserializer implements CustomJsonDeserializer<ParamsRequiredTweaksConfig> {
        @Override
        public ParamsRequiredTweaksConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            checkKeys(Set.of(
                            "auto_discarding_fire_ball",
                            "stuffed_shulker_box_stack_limit",
                            "shulker_box_nesting_limit",
                            "warden_attributes_weakening",
                            "warden_sonic_boom_weakening"),
                    jsonObject.keySet(),
                    true
            );
            AutoDiscardingFireBallConfig autoDiscardingFireBallConfig = context.deserialize(jsonObject.get("auto_discarding_fire_ball"), AutoDiscardingFireBallConfig.class);
            StuffedShulkerBoxStackLimitConfig stuffedShulkerBoxStackLimitConfig = context.deserialize(jsonObject.get("stuffed_shulker_box_stack_limit"), StuffedShulkerBoxStackLimitConfig.class);
            ShulkerBoxNestingLimitConfig shulkerBoxNestingLimitConfig = context.deserialize(jsonObject.get("shulker_box_nesting_limit"), ShulkerBoxNestingLimitConfig.class);
            WardenAttributesWeakeningConfig wardenAttributesWeakeningConfig = context.deserialize(jsonObject.get("warden_attributes_weakening"), WardenAttributesWeakeningConfig.class);
            WardenSonicBoomWeakeningConfig wardenSonicBoomWeakeningConfig = context.deserialize(jsonObject.get("warden_sonic_boom_weakening"), WardenSonicBoomWeakeningConfig.class);
            return new ParamsRequiredTweaksConfig(
                    autoDiscardingFireBallConfig,
                    stuffedShulkerBoxStackLimitConfig,
                    shulkerBoxNestingLimitConfig,
                    wardenAttributesWeakeningConfig,
                    wardenSonicBoomWeakeningConfig
            );
        }
    }
}
