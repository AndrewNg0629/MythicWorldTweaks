package top.aenp.mwt.config.v2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Rarity;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Optional;

public record ModConfig(
        boolean modEnabled,
        boolean multiplayerSupportEnabled,
        ModIdValidationConfig modIdValidationConfig,
        Tweaks tweaks,
        ItemEditorConfig itemEditorConfig,
        int configVersion
) {
    public static final Codec<ModConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("mod_enabled").forGetter(ModConfig::modEnabled),
                    Codec.BOOL.fieldOf("multiplayer_support_enabled").forGetter(ModConfig::multiplayerSupportEnabled),
                    ModIdValidationConfig.CODEC.fieldOf("mod_id_validation").forGetter(ModConfig::modIdValidationConfig),
                    Tweaks.CODEC.fieldOf("tweaks").forGetter(ModConfig::tweaks),
                    ItemEditorConfig.CODEC.fieldOf("item_editor").forGetter(ModConfig::itemEditorConfig),
                    Codec.INT.fieldOf("config_version").forGetter(ModConfig::configVersion)
            ).apply(instance, ModConfig::new)
    );
    public static final ModConfig DEFAULT_CONFIG = new ModConfig(
            true,
            true,
            new ModIdValidationConfig(false, List.of(), List.of()),
            new Tweaks(
                    false,
                    new Tweaks.LocalToggleTweaks1(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false),
                    new Tweaks.SyncedToggleTweaks1(false, false, false),
                    new Tweaks.ValueTweaks(
                            new Tweaks.ValueTweaks.FireballAutoDiscarding(false, 200),
                            new Tweaks.ValueTweaks.StuffedShulkerBoxStacking(false, 1),
                            new Tweaks.ValueTweaks.ShulkerBoxNesting(false, 2),
                            new Tweaks.ValueTweaks.WardenAttributesControl(false, 500.0, 1.0, 30.0, 1.5, 0.3, 1.2, 18),
                            new Tweaks.ValueTweaks.WardenSonicBoomControl(false, true, 10.0, 1.0, 34),
                            new Tweaks.ValueTweaks.PlayerDeathItemProtection(false, 12000, false, false),
                            new Tweaks.ValueTweaks.VaultReuse(false, 300, false, 600, false),
                            new Tweaks.ValueTweaks.ItemExplosionResistance(false, List.of())
                    )
            ),
            new ItemEditorConfig(false, List.of()),
            0
    );

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

    public record Tweaks(boolean localTweaksEnabled, LocalToggleTweaks1 localToggleTweaks1,
                         SyncedToggleTweaks1 syncedToggleTweaks1, ValueTweaks valueTweaks) {
        public static final Codec<Tweaks> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("local_tweaks_enabled").forGetter(Tweaks::localTweaksEnabled),
                        LocalToggleTweaks1.CODEC.forGetter(Tweaks::localToggleTweaks1),
                        SyncedToggleTweaks1.CODEC.forGetter(Tweaks::syncedToggleTweaks1),
                        ValueTweaks.CODEC.forGetter(Tweaks::valueTweaks)
                ).apply(instance, Tweaks::new)
        );

        public record LocalToggleTweaks1(
                boolean throwableFireCharge,
                boolean creepersDontBreakBlocks,
                boolean playerRiding,
                boolean playerRidingFallProtection,
                boolean dispensableTridents,
                boolean tridentsReturnFromVoid,
                boolean tridentsDamageMultipleTimes,
                boolean thrownTridentsPersist,
                boolean villagersAlwaysZombify,
                boolean keepExperienceAfterDeath,
                boolean alwaysDropDragonEgg,
                boolean armorTrimPacify,
                boolean carpetFakePlayerSleepExclusion,
                boolean editablePlayerData,
                boolean creativePlayerVoidResistance
        ) {
            public static final MapCodec<LocalToggleTweaks1> CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("throwable_fire_charge").forGetter(LocalToggleTweaks1::throwableFireCharge),
                            Codec.BOOL.fieldOf("creepers_dont_break_blocks").forGetter(LocalToggleTweaks1::creepersDontBreakBlocks),
                            Codec.BOOL.fieldOf("player_riding").forGetter(LocalToggleTweaks1::playerRiding),
                            Codec.BOOL.fieldOf("player_riding_fall_protection").forGetter(LocalToggleTweaks1::playerRidingFallProtection),
                            Codec.BOOL.fieldOf("dispensable_tridents").forGetter(LocalToggleTweaks1::dispensableTridents),
                            Codec.BOOL.fieldOf("tridents_return_from_void").forGetter(LocalToggleTweaks1::tridentsReturnFromVoid),
                            Codec.BOOL.fieldOf("tridents_damage_multiple_times").forGetter(LocalToggleTweaks1::tridentsDamageMultipleTimes),
                            Codec.BOOL.fieldOf("thrown_tridents_persist").forGetter(LocalToggleTweaks1::thrownTridentsPersist),
                            Codec.BOOL.fieldOf("villagers_always_zombify").forGetter(LocalToggleTweaks1::villagersAlwaysZombify),
                            Codec.BOOL.fieldOf("keep_experience_after_death").forGetter(LocalToggleTweaks1::keepExperienceAfterDeath),
                            Codec.BOOL.fieldOf("always_drop_dragon_egg").forGetter(LocalToggleTweaks1::alwaysDropDragonEgg),
                            Codec.BOOL.fieldOf("armor_trim_pacify").forGetter(LocalToggleTweaks1::armorTrimPacify),
                            Codec.BOOL.fieldOf("carpet_fake_player_sleep_exclusion").forGetter(LocalToggleTweaks1::carpetFakePlayerSleepExclusion),
                            Codec.BOOL.fieldOf("editable_player_data").forGetter(LocalToggleTweaks1::editablePlayerData),
                            Codec.BOOL.fieldOf("creative_player_void_resistance").forGetter(LocalToggleTweaks1::creativePlayerVoidResistance)
                    ).apply(instance, LocalToggleTweaks1::new)
            );
        }

        public record SyncedToggleTweaks1(
                boolean largeFireCharge,
                boolean suicideCommand,
                boolean bedIdle
        ) {
            public static final MapCodec<SyncedToggleTweaks1> CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Codec.BOOL.fieldOf("large_fire_charge").forGetter(SyncedToggleTweaks1::largeFireCharge),
                            Codec.BOOL.fieldOf("suicide_command").forGetter(SyncedToggleTweaks1::suicideCommand),
                            Codec.BOOL.fieldOf("bed_idle").forGetter(SyncedToggleTweaks1::bedIdle)
                    ).apply(instance, SyncedToggleTweaks1::new)
            );
        }

        public record ValueTweaks(
                FireballAutoDiscarding fireballAutoDiscarding,
                StuffedShulkerBoxStacking stuffedShulkerBoxStacking,
                ShulkerBoxNesting shulkerBoxNesting,
                WardenAttributesControl wardenAttributesControl,
                WardenSonicBoomControl wardenSonicBoomControl,
                PlayerDeathItemProtection playerDeathItemProtection,
                VaultReuse vaultReuse,
                ItemExplosionResistance itemExplosionResistance
        ) {
            public static final MapCodec<ValueTweaks> CODEC = RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            FireballAutoDiscarding.CODEC.fieldOf("fireball_auto_discarding").forGetter(ValueTweaks::fireballAutoDiscarding),
                            StuffedShulkerBoxStacking.CODEC.fieldOf("stuffed_shulker_box_stacking").forGetter(ValueTweaks::stuffedShulkerBoxStacking),
                            ShulkerBoxNesting.CODEC.fieldOf("shulker_box_nesting").forGetter(ValueTweaks::shulkerBoxNesting),
                            WardenAttributesControl.CODEC.fieldOf("warden_attributes_control").forGetter(ValueTweaks::wardenAttributesControl),
                            WardenSonicBoomControl.CODEC.fieldOf("warden_sonic_boom_control").forGetter(ValueTweaks::wardenSonicBoomControl),
                            PlayerDeathItemProtection.CODEC.fieldOf("player_death_item_protection").forGetter(ValueTweaks::playerDeathItemProtection),
                            VaultReuse.CODEC.fieldOf("vault_reuse").forGetter(ValueTweaks::vaultReuse),
                            ItemExplosionResistance.CODEC.fieldOf("item_explosion_resistance").forGetter(ValueTweaks::itemExplosionResistance)
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
                    double baseMovementSpeed,
                    double chasingMovementSpeedMultiplier,
                    int attackIntervalTicks
            ) {
                public static final Codec<WardenAttributesControl> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                Codec.BOOL.fieldOf("enabled").forGetter(WardenAttributesControl::enabled),
                                rangedDouble(0.0, Double.MAX_VALUE, false, true).fieldOf("max_health").forGetter(WardenAttributesControl::maxHealth),
                                rangedDouble(0.0, 1.0, true, true).fieldOf("knockback_resistance").forGetter(WardenAttributesControl::knockbackResistance),
                                rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("melee_attack_damage").forGetter(WardenAttributesControl::meleeAttackDamage),
                                rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("melee_attack_knockback").forGetter(WardenAttributesControl::meleeAttackKnockback),
                                rangedDouble(0.0, Double.MAX_VALUE, false, false).fieldOf("base_movement_speed").forGetter(WardenAttributesControl::baseMovementSpeed),
                                rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("chasing_movement_speed_multiplier").forGetter(WardenAttributesControl::chasingMovementSpeedMultiplier),
                                Codecs.NONNEGATIVE_INT.fieldOf("attack_interval_ticks").forGetter(WardenAttributesControl::attackIntervalTicks)
                        ).apply(instance, WardenAttributesControl::new)
                );
            }

            public record WardenSonicBoomControl(
                    boolean enabled,
                    boolean sonicBoomEnabled,
                    double sonicBoomDamage,
                    double sonicBoomKnockbackMultiplier,
                    int sonicBoomIntervalTicks
            ) {
                public static final Codec<WardenSonicBoomControl> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                Codec.BOOL.fieldOf("enabled").forGetter(WardenSonicBoomControl::enabled),
                                Codec.BOOL.fieldOf("sonic_boom_enabled").forGetter(WardenSonicBoomControl::sonicBoomEnabled),
                                rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("sonic_boom_damage").forGetter(WardenSonicBoomControl::sonicBoomDamage),
                                rangedDouble(0.0, 1.0, true, true).fieldOf("sonic_boom_knockback_multiplier").forGetter(WardenSonicBoomControl::sonicBoomKnockbackMultiplier),
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

            public record VaultReuse(
                    boolean reuseRegularVault,
                    int regularVaultCooldown,
                    boolean reuseOminousVault,
                    int ominousVaultCooldown,
                    boolean wthitIntegration
            ) {
                public static final Codec<VaultReuse> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                Codec.BOOL.fieldOf("reuse_regular_vault").forGetter(VaultReuse::reuseRegularVault),
                                Codec.INT.fieldOf("regular_vault_cooldown").forGetter(VaultReuse::regularVaultCooldown),
                                Codec.BOOL.fieldOf("reuse_ominous_vault").forGetter(VaultReuse::reuseOminousVault),
                                Codec.INT.fieldOf("ominous_vault_cooldown").forGetter(VaultReuse::ominousVaultCooldown),
                                Codec.BOOL.fieldOf("wthit_integration").forGetter(VaultReuse::wthitIntegration)
                        ).apply(instance, VaultReuse::new)
                );
            }

            public record ItemExplosionResistance(
                    boolean enabled,
                    List<RegistryEntry<Item>> inclusionList
            ) {
                public static final Codec<ItemExplosionResistance> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                Codec.BOOL.fieldOf("enabled").forGetter(ItemExplosionResistance::enabled),
                                ConfigManager.ITEM_ENTRY_CODEC.listOf().fieldOf("inclusion_list").forGetter(ItemExplosionResistance::inclusionList)
                        ).apply(instance, ItemExplosionResistance::new)
                );
            }
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
                                    ConfigManager.ITEM_ENTRY_CODEC.fieldOf("item").forGetter(ItemEditorUnit::itemEntry),
                                    Codec.INT.optionalFieldOf("max_stack_size").forGetter(ItemEditorUnit::maxStackSize),
                                    Codec.INT.optionalFieldOf("durability").forGetter(ItemEditorUnit::durability),
                                    Codec.BOOL.optionalFieldOf("fire_resistant").forGetter(ItemEditorUnit::fireResistant),
                                    Rarity.CODEC.optionalFieldOf("rarity").forGetter(ItemEditorUnit::rarity),
                                    ConfigManager.ITEM_ENTRY_CODEC.optionalFieldOf("crafting_remains").forGetter(ItemEditorUnit::craftingRemainsEntry),
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
                    throw new IllegalArgumentException(String.format("Item %s is not damageable, you can't make it damageable. Drop the config key \"durability\".", itemEntry.getIdAsString()));
                }
            }

            public record WrappedFoodComponents(
                    int nutrition,
                    double saturation,
                    boolean canAlwaysEat,
                    double eatSeconds,
                    Optional<RegistryEntry<Item>> eatingRemains,
                    List<WrappedStatusEffectEntry> effects
            ) {
                public static final Codec<WrappedFoodComponents> CODEC = RecordCodecBuilder.create(
                        instance -> instance.group(
                                Codecs.NONNEGATIVE_INT.fieldOf("nutrition").forGetter(WrappedFoodComponents::nutrition),
                                rangedDouble(0.0, Double.MAX_VALUE, true, true).fieldOf("saturation").forGetter(WrappedFoodComponents::saturation),
                                Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(WrappedFoodComponents::canAlwaysEat),
                                rangedDouble(0.0, Double.MAX_VALUE, false, true).optionalFieldOf("eat_seconds", 1.6D).forGetter(WrappedFoodComponents::eatSeconds),
                                ConfigManager.ITEM_ENTRY_CODEC.optionalFieldOf("eating_remains").forGetter(WrappedFoodComponents::eatingRemains),
                                WrappedStatusEffectEntry.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(WrappedFoodComponents::effects)
                        ).apply(instance, WrappedFoodComponents::new)
                );

                public FoodComponent createComponents() {
                    Optional<ItemStack> remains = eatingRemains.map(entry -> entry.value().equals(Items.AIR) ? null : new ItemStack(entry.value()));
                    List<FoodComponent.StatusEffectEntry> entries = effects.stream().map(WrappedStatusEffectEntry::createEntry).toList();
                    return new FoodComponent(nutrition, (float) saturation, canAlwaysEat, (float) eatSeconds, remains, entries);
                }

                public record WrappedStatusEffectEntry(
                        RegistryEntry<StatusEffect> effect,
                        int level,
                        int duration,
                        double probability
                ) {
                    public static final Codec<WrappedStatusEffectEntry> CODEC = RecordCodecBuilder.create(
                            instance -> instance.group(
                                    StatusEffect.ENTRY_CODEC.fieldOf("effect").forGetter(WrappedStatusEffectEntry::effect),
                                    Codec.INT.optionalFieldOf("level", 1).forGetter(WrappedStatusEffectEntry::level),
                                    Codec.INT.fieldOf("duration").forGetter(WrappedStatusEffectEntry::duration),
                                    Codec.DOUBLE.optionalFieldOf("probability", 1.0D).forGetter(WrappedStatusEffectEntry::probability)
                            ).apply(instance, WrappedStatusEffectEntry::new)
                    );

                    public FoodComponent.StatusEffectEntry createEntry() {
                        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(effect, duration * 20, level - 1);
                        return new FoodComponent.StatusEffectEntry(statusEffectInstance, (float) probability);
                    }
                }
            }
        }
    }
}
