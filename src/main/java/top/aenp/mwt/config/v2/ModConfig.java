package top.aenp.mwt.config.v2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Rarity;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Optional;

public record ModConfig(
        boolean modEnabled,
        boolean localTweaksEnabled,
        boolean multiplayerSupportEnabled,
        ModIdValidationConfig modIdValidationConfig,
        ToggleTweaksSection1 toggleTweaksSection1,
        ToggleTweaksSection2 toggleTweaksSection2,
        ValueTweaks valueTweaks,
        ItemEditorConfig itemEditorConfig
) {
    static {
        ITEM_ENTRY_CODEC = Registries.ITEM.getEntryCodec();
    }

    public static final Codec<ModConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("mod_enabled").forGetter(ModConfig::modEnabled),
                    Codec.BOOL.fieldOf("local_tweaks_enabled").forGetter(ModConfig::localTweaksEnabled),
                    Codec.BOOL.fieldOf("multiplayer_support_enabled").forGetter(ModConfig::multiplayerSupportEnabled),
                    ModIdValidationConfig.CODEC.fieldOf("mod_id_validation").forGetter(ModConfig::modIdValidationConfig),
                    ToggleTweaksSection1.CODEC.fieldOf("toggle_tweaks_1").forGetter(ModConfig::toggleTweaksSection1),
                    ToggleTweaksSection2.CODEC.fieldOf("toggle_tweaks_2").forGetter(ModConfig::toggleTweaksSection2),
                    ValueTweaks.CODEC.fieldOf("value_tweaks").forGetter(ModConfig::valueTweaks),
                    ItemEditorConfig.CODEC.fieldOf("item_editor").forGetter(ModConfig::itemEditorConfig)
            ).apply(instance, ModConfig::new)
    );

    public static final Codec<RegistryEntry<Item>> ITEM_ENTRY_CODEC;

    public static Codec<Double> rangedDouble(double min, double max, boolean leftInclusive, boolean rightInclusive) {
        return Codec.DOUBLE
                .validate(
                        value -> {
                            boolean leftPass = value.compareTo(min) > 0 || (Math.abs(value - min) < 1e-6d && leftInclusive);
                            boolean rightPass = value.compareTo(max) < 0 || (Math.abs(value - max) < 1e-6d && rightInclusive);
                            if (leftPass && rightPass) {
                                return DataResult.success(value);
                            } else {
                                return DataResult.error(() -> String.format("Value must be within range %s%s,%s%s: %s", leftInclusive ? "[" : "(", min, max, rightInclusive ? "]" : ")", value));
                            }
                        }
                );
    }

    public record ModIdValidationConfig(
            boolean enabled,
            List<String> requiredMods,
            List<String> prohibitedMods
    ) {
        public static final Codec<ModIdValidationConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("enabled").forGetter(ModIdValidationConfig::enabled),
                        Codec.STRING.listOf().fieldOf("required_mods").forGetter(ModIdValidationConfig::requiredMods),
                        Codec.STRING.listOf().fieldOf("prohibited_mods").forGetter(ModIdValidationConfig::prohibitedMods)
                ).apply(instance, ModIdValidationConfig::new)
        );
    }

    public record ToggleTweaksSection1(
            boolean throwableFireCharge,
            boolean largeFireCharge,
            boolean creepersDontBreakBlocks,
            boolean itemExplosionResistance,
            boolean playerRiding,
            boolean playerRidingFallProtection,
            boolean dispensableTridents,
            boolean tridentsReturnFromVoid,
            boolean tridentsDamageMultipleTimes,
            boolean thrownTridentsPersist,
            boolean villagersAlwaysZombify,
            boolean bedIdle,
            boolean keepExperienceAfterDeath,
            boolean alwaysDropDragonEgg,
            boolean armorTrimPacify,
            boolean suicideCommand
    ) {
        public static final Codec<ToggleTweaksSection1> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("throwable_fire_charge").forGetter(ToggleTweaksSection1::throwableFireCharge),
                        Codec.BOOL.fieldOf("large_fire_charge").forGetter(ToggleTweaksSection1::largeFireCharge),
                        Codec.BOOL.fieldOf("creepers_dont_break_blocks").forGetter(ToggleTweaksSection1::creepersDontBreakBlocks),
                        Codec.BOOL.fieldOf("item_explosion_resistance").forGetter(ToggleTweaksSection1::itemExplosionResistance),
                        Codec.BOOL.fieldOf("player_riding").forGetter(ToggleTweaksSection1::playerRiding),
                        Codec.BOOL.fieldOf("player_riding_fall_protection").forGetter(ToggleTweaksSection1::playerRidingFallProtection),
                        Codec.BOOL.fieldOf("dispensable_tridents").forGetter(ToggleTweaksSection1::dispensableTridents),
                        Codec.BOOL.fieldOf("tridents_return_from_void").forGetter(ToggleTweaksSection1::tridentsReturnFromVoid),
                        Codec.BOOL.fieldOf("tridents_damage_multiple_times").forGetter(ToggleTweaksSection1::tridentsDamageMultipleTimes),
                        Codec.BOOL.fieldOf("thrown_tridents_persist").forGetter(ToggleTweaksSection1::thrownTridentsPersist),
                        Codec.BOOL.fieldOf("villagers_always_zombify").forGetter(ToggleTweaksSection1::villagersAlwaysZombify),
                        Codec.BOOL.fieldOf("bed_idle").forGetter(ToggleTweaksSection1::bedIdle),
                        Codec.BOOL.fieldOf("keep_experience_after_death").forGetter(ToggleTweaksSection1::keepExperienceAfterDeath),
                        Codec.BOOL.fieldOf("always_drop_dragon_egg").forGetter(ToggleTweaksSection1::alwaysDropDragonEgg),
                        Codec.BOOL.fieldOf("armor_trim_pacify").forGetter(ToggleTweaksSection1::armorTrimPacify),
                        Codec.BOOL.fieldOf("suicide_command").forGetter(ToggleTweaksSection1::suicideCommand)
                ).apply(instance, ToggleTweaksSection1::new)
        );
    }

    public record ToggleTweaksSection2(
            boolean carpetFakePlayerSleepExclusion,
            boolean editablePlayerData,
            boolean creativePlayerVoidResistance
    ) {
        public static final Codec<ToggleTweaksSection2> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("carpet_fake_player_sleep_exclusion").forGetter(ToggleTweaksSection2::carpetFakePlayerSleepExclusion),
                        Codec.BOOL.fieldOf("editable_player_data").forGetter(ToggleTweaksSection2::editablePlayerData),
                        Codec.BOOL.fieldOf("creative_player_void_resistance").forGetter(ToggleTweaksSection2::creativePlayerVoidResistance)
                ).apply(instance, ToggleTweaksSection2::new)
        );
    }

    public record ValueTweaks(
            FireballAutoDiscarding fireballAutoDiscarding,
            StuffedShulkerBoxStacking stuffedShulkerBoxStacking,
            ShulkerBoxNesting shulkerBoxNesting,
            WardenAttributesControl wardenAttributesControl,
            WardenSonicBoomControl wardenSonicBoomControl,
            PlayerDeathItemProtection playerDeathItemProtection
    ) {
        public static final Codec<ValueTweaks> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        FireballAutoDiscarding.CODEC.fieldOf("fireball_auto_discarding").forGetter(ValueTweaks::fireballAutoDiscarding),
                        StuffedShulkerBoxStacking.CODEC.fieldOf("stuffed_shulker_box_stacking").forGetter(ValueTweaks::stuffedShulkerBoxStacking),
                        ShulkerBoxNesting.CODEC.fieldOf("shulker_box_nesting").forGetter(ValueTweaks::shulkerBoxNesting),
                        WardenAttributesControl.CODEC.fieldOf("warden_attributes_control").forGetter(ValueTweaks::wardenAttributesControl),
                        WardenSonicBoomControl.CODEC.fieldOf("warden_sonic_boom_control").forGetter(ValueTweaks::wardenSonicBoomControl),
                        PlayerDeathItemProtection.CODEC.fieldOf("player_death_item_protection").forGetter(ValueTweaks::playerDeathItemProtection)
                ).apply(instance, ValueTweaks::new)
        );

        public record FireballAutoDiscarding(
                boolean enabled,
                int discardTicks
        ) {
            public static final Codec<FireballAutoDiscarding> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("enabled").forGetter(FireballAutoDiscarding::enabled),
                            Codecs.POSITIVE_INT.fieldOf("discard_ticks").forGetter(FireballAutoDiscarding::discardTicks)
                    ).apply(instance, FireballAutoDiscarding::new)
            );
        }

        public record StuffedShulkerBoxStacking(
                boolean enabled,
                int maxStackSize
        ) {
            public static final Codec<StuffedShulkerBoxStacking> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("enabled").forGetter(StuffedShulkerBoxStacking::enabled),
                            Codecs.POSITIVE_INT.fieldOf("max_stack_size").forGetter(StuffedShulkerBoxStacking::maxStackSize)
                    ).apply(instance, StuffedShulkerBoxStacking::new)
            );
        }

        public record ShulkerBoxNesting(
                boolean enabled,
                int maxLayers
        ) {
            public static final Codec<ShulkerBoxNesting> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("enabled").forGetter(ShulkerBoxNesting::enabled),
                            Codecs.POSITIVE_INT.fieldOf("max_layers").forGetter(ShulkerBoxNesting::maxLayers)
                    ).apply(instance, ShulkerBoxNesting::new)
            );
        }

        public record WardenAttributesControl(
                boolean enabled,
                double maxHealth,
                double knockbackResistance,
                double meleeAttackDamage,
                double meleeAttackKnockback,
                double chasingMovementSpeed,
                int attackIntervalTicks
        ) {
            public static final Codec<WardenAttributesControl> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("enabled").forGetter(WardenAttributesControl::enabled),
                            rangedDouble(0.0, Double.MAX_VALUE, false, true).fieldOf("max_health").forGetter(WardenAttributesControl::maxHealth),
                            rangedDouble(0.0, 1.0, true, true).fieldOf("knockback_resistance").forGetter(WardenAttributesControl::knockbackResistance),
                            rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("melee_attack_damage").forGetter(WardenAttributesControl::meleeAttackDamage),
                            rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("melee_attack_knockback").forGetter(WardenAttributesControl::meleeAttackKnockback),
                            rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("chasing_movement_speed").forGetter(WardenAttributesControl::chasingMovementSpeed),
                            Codecs.NONNEGATIVE_INT.fieldOf("attack_interval_ticks").forGetter(WardenAttributesControl::attackIntervalTicks)
                    ).apply(instance, WardenAttributesControl::new)
            );
        }

        public record WardenSonicBoomControl(
                boolean enabled,
                boolean sonicBoomEnabled,
                double sonicBoomDamage,
                double sonicBoomKnockbackFactor,
                int sonicBoomIntervalTicks
        ) {
            public static final Codec<WardenSonicBoomControl> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("enabled").forGetter(WardenSonicBoomControl::enabled),
                            Codec.BOOL.fieldOf("sonic_boom_enabled").forGetter(WardenSonicBoomControl::sonicBoomEnabled),
                            rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("sonic_boom_damage").forGetter(WardenSonicBoomControl::sonicBoomDamage),
                            rangedDouble(0.0, 1.0, true, true).fieldOf("sonic_boom_knockback_factor").forGetter(WardenSonicBoomControl::sonicBoomKnockbackFactor),
                            Codecs.NONNEGATIVE_INT.fieldOf("sonic_boom_interval_ticks").forGetter(WardenSonicBoomControl::sonicBoomIntervalTicks)
                    ).apply(instance, WardenSonicBoomControl::new)
            );
        }

        public record PlayerDeathItemProtection(
                boolean enabled,
                int itemDespawnTicks,
                boolean preventMobPickup,
                boolean strictPickup
        ) {
            public static final Codec<PlayerDeathItemProtection> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("enabled").forGetter(PlayerDeathItemProtection::enabled),
                            Codecs.NONNEGATIVE_INT.fieldOf("item_despawn_ticks").forGetter(PlayerDeathItemProtection::itemDespawnTicks),
                            Codec.BOOL.fieldOf("prevent_mob_pickup").forGetter(PlayerDeathItemProtection::preventMobPickup),
                            Codec.BOOL.fieldOf("strict_pickup").forGetter(PlayerDeathItemProtection::strictPickup)
                    ).apply(instance, PlayerDeathItemProtection::new)
            );
        }
    }

    public record ItemEditorConfig(
            boolean enabled,
            List<ItemEditorUnit> units
    ) {
        public static final Codec<ItemEditorConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("enabled").forGetter(ItemEditorConfig::enabled),
                        ItemEditorUnit.CODEC.listOf().fieldOf("item_edits").forGetter(ItemEditorConfig::units)
                ).apply(instance, ItemEditorConfig::new)
        );

        public record ItemEditorUnit(
                RegistryEntry<Item> itemEntry,
                Optional<Integer> maxStackSize,
                Optional<Integer> durability,
                Optional<Boolean> fireResistant,
                Optional<Rarity> rarity,
                Optional<RegistryEntry<Item>> craftingRemainsEntry,
                Optional<Boolean> isFood,
                Optional<WrappedFoodComponents> wrappedFoodComponents
        ) {
            public static final Codec<ItemEditorUnit> CODEC = Codecs.exceptionCatching(
                    RecordCodecBuilder.create(
                            instance -> instance.group(
                                    ITEM_ENTRY_CODEC.fieldOf("item").forGetter(ItemEditorUnit::itemEntry),
                                    Codec.INT.optionalFieldOf("msx_stack_size").forGetter(ItemEditorUnit::maxStackSize),
                                    Codec.INT.optionalFieldOf("durability").forGetter(ItemEditorUnit::durability),
                                    Codec.BOOL.optionalFieldOf("fire_resistant").forGetter(ItemEditorUnit::fireResistant),
                                    Rarity.CODEC.optionalFieldOf("rarity").forGetter(ItemEditorUnit::rarity),
                                    ITEM_ENTRY_CODEC.optionalFieldOf("crafting_remains").forGetter(ItemEditorUnit::craftingRemainsEntry),
                                    Codec.BOOL.optionalFieldOf("is_food").forGetter(ItemEditorUnit::isFood),
                                    WrappedFoodComponents.CODEC.optionalFieldOf("food_components").forGetter(ItemEditorUnit::wrappedFoodComponents)
                            ).apply(instance, ItemEditorUnit::new)
                    )
            );

            public ItemEditorUnit {
                Item item = itemEntry.value();
                boolean itemDamageable = item.getComponents().contains(DataComponentTypes.MAX_DAMAGE);
                if (itemDamageable && maxStackSize.isPresent()) {
                    throw new IllegalArgumentException(String.format("Item %s is damageable, so can't be stacked. Drop the config key \"max_stack_size\".", itemEntry.getIdAsString()));
                }
                if (!itemDamageable && durability.isPresent()) {
                    throw new IllegalArgumentException(String.format("Item %s not damageable, you can't make it damageable. Drop the config key \"durability\".", itemEntry.getIdAsString()));
                }
            }

            public record WrappedFoodComponents(
                    int nutrition,
                    double saturation,
                    boolean canAlwaysEat,
                    double eatSeconds,
                    Optional<RegistryEntry<Item>> eatingRemains,
                    List<FoodComponent.StatusEffectEntry> effects
            ) {
                public static final Codec<WrappedFoodComponents> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                Codecs.NONNEGATIVE_INT.fieldOf("nutrition").forGetter(WrappedFoodComponents::nutrition),
                                rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("saturation").forGetter(WrappedFoodComponents::saturation),
                                Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(WrappedFoodComponents::canAlwaysEat),
                                rangedDouble(0.0, Double.MAX_VALUE, false, true).optionalFieldOf("eat_seconds", 1.6D).forGetter(WrappedFoodComponents::eatSeconds),
                                ITEM_ENTRY_CODEC.optionalFieldOf("eating_remains").forGetter(WrappedFoodComponents::eatingRemains),
                                FoodComponent.StatusEffectEntry.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(WrappedFoodComponents::effects)
                        ).apply(instance, WrappedFoodComponents::new)
                );

                public FoodComponent createComponents() {
                    Optional<ItemStack> remains = this.eatingRemains.map(entry -> new ItemStack(entry.value()));
                    return new FoodComponent(this.nutrition, (float) this.saturation, this.canAlwaysEat, (float) this.eatSeconds, remains, effects);
                }
            }
        }
    }
}
