package online.andrew2007.mythic.config;

import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.configFileParser.BinaryToggleTweaksConfig;
import online.andrew2007.mythic.config.configFileParser.ItemEditorConfig;
import online.andrew2007.mythic.config.configFileParser.ModConfig;
import online.andrew2007.mythic.config.configFileParser.ParamsRequiredTweaksConfig;
import online.andrew2007.mythic.config.runtimeParams.LocalRuntimeParams;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import online.andrew2007.mythic.item.ItemEditor;
import online.andrew2007.mythic.util.WardenEntityUtil;

import java.util.HashSet;

public class RuntimeController {
    private static LocalRuntimeParams localRuntimeParams = LocalRuntimeParams.getDefaultInstance();
    private static boolean localRuntimeParamsInitialized = false;
    private static TransmittableRuntimeParams currentTParams = TransmittableRuntimeParams.getDefaultInstance();
    private static TransmittableRuntimeParams serverPlayCachedTParams = null;

    static void loadTransmittableParamsFromConfig() {
        ModConfig modConfig = ConfigLoader.getCurrentModConfig();
        TransmittableRuntimeParams newParams;
        TransmittableRuntimeParams previousParams = currentTParams;
        if (modConfig.tweaksEnabled() && localRuntimeParams.modEnabled()) {
            BinaryToggleTweaksConfig binaryToggleTweaksConfig = modConfig.binaryToggleTweaksConfig();
            ParamsRequiredTweaksConfig paramsRequiredTweaksConfig = modConfig.paramsRequiredTweaksConfig();
            TransmittableRuntimeParams.TransmittableIECUnit[] transmittableIECUnits;
            boolean itemEditorEnabled = modConfig.itemEditorConfig().enabled();
            if (itemEditorEnabled) {
                ItemEditorConfig.ItemEditorConfigUnit[] itemEditorConfigUnits = modConfig.itemEditorConfig().itemEditorConfigUnits();
                transmittableIECUnits = new TransmittableRuntimeParams.TransmittableIECUnit[itemEditorConfigUnits.length];
                for (int i = 0; i < itemEditorConfigUnits.length; i++) {
                    ItemEditorConfig.ItemEditorConfigUnit itemEditorConfigUnit = itemEditorConfigUnits[i];
                    transmittableIECUnits[i] = new TransmittableRuntimeParams.TransmittableIECUnit(
                            itemEditorConfigUnit.targetItem(),
                            itemEditorConfigUnit.maxStackSize(),
                            itemEditorConfigUnit.maxDamage(),
                            itemEditorConfigUnit.fireResistant(),
                            itemEditorConfigUnit.rarity(),
                            itemEditorConfigUnit.recipeRemainder(),
                            itemEditorConfigUnit.foodProperty()
                    );
                }
            } else {
                transmittableIECUnits = null;
            }
            newParams = new TransmittableRuntimeParams(
                    binaryToggleTweaksConfig.throwableFireCharge(),
                    binaryToggleTweaksConfig.largeFireCharge(),
                    binaryToggleTweaksConfig.editablePlayerData(),
                    binaryToggleTweaksConfig.blocksFriendlyCreepers(),
                    binaryToggleTweaksConfig.itemExplosionResistance(),
                    binaryToggleTweaksConfig.nestableShulkerBoxes(),
                    binaryToggleTweaksConfig.rideablePlayers(),
                    binaryToggleTweaksConfig.playerRidingGestures(),
                    binaryToggleTweaksConfig.playerRidingProtection(),
                    binaryToggleTweaksConfig.dispensableTridents(),
                    binaryToggleTweaksConfig.voidReturnableTrident(),
                    binaryToggleTweaksConfig.multiTridentDamage(),
                    binaryToggleTweaksConfig.persistentTridents(),
                    binaryToggleTweaksConfig.silentWardens(),
                    binaryToggleTweaksConfig.constantVillagerConversion(),
                    binaryToggleTweaksConfig.fakePlayerSleepExclusion(),
                    binaryToggleTweaksConfig.sleepingExtras(),
                    paramsRequiredTweaksConfig.autoDiscardingFireBallConfig().enabled(),
                    paramsRequiredTweaksConfig.stuffedShulkerBoxStackLimitConfig().enabled(),
                    paramsRequiredTweaksConfig.shulkerBoxNestingLimitConfig().enabled(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().enabled(),
                    paramsRequiredTweaksConfig.wardenSonicBoomWeakeningConfig().enabled(),
                    paramsRequiredTweaksConfig.autoDiscardingFireBallConfig().maxLifeTicks(),
                    paramsRequiredTweaksConfig.stuffedShulkerBoxStackLimitConfig().maxStackSize(),
                    paramsRequiredTweaksConfig.shulkerBoxNestingLimitConfig().maxLayers(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().maxHealth(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().knockBackResistance(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().meleeAttackDamage(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().meleeAttackKnockBack(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().idleMovementSpeed(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().chasingMovementSpeed(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().attackIntervalTicks(),
                    paramsRequiredTweaksConfig.wardenSonicBoomWeakeningConfig().sonicBoomDamage(),
                    paramsRequiredTweaksConfig.wardenSonicBoomWeakeningConfig().sonicBoomKnockBackRate(),
                    paramsRequiredTweaksConfig.wardenSonicBoomWeakeningConfig().sonicBoomIntervalTicks(),
                    itemEditorEnabled,
                    transmittableIECUnits
            );
        } else {
            newParams = TransmittableRuntimeParams.getDefaultInstance();
        }
        if (localRuntimeParams.modEnabled()) {
            onParamsChange(previousParams, newParams);
        }
    }

    public static void loadLocalParamsFromConfig(ModConfig modConfig) {
        if (!localRuntimeParamsInitialized) {
            localRuntimeParams = new LocalRuntimeParams(
                    modConfig.modEnabled(),
                    modConfig.modDataPackEnabled(),
                    modConfig.serverPlaySupportEnabled(),
                    modConfig.modIdValidationConfig().enabled(),
                    modConfig.modIdValidationConfig().modIdList()
            );
            localRuntimeParamsInitialized = true;
        }
    }

    private static void onParamsChange(TransmittableRuntimeParams previousParams, TransmittableRuntimeParams newParams) {
        currentTParams = newParams;
        applyItemEdits(previousParams.itemEditorConfig());
        WardenEntityUtil.modifyWardenAttributes();
        WardenEntityUtil.WardenEntityTrack.wardenRefresh();
    }
    private static void applyItemEdits(TransmittableRuntimeParams.TransmittableIECUnit[] previousUnits) {
        HashSet<ItemEditor> itemEditors = new HashSet<>();
        if (previousUnits != null) {
            for (TransmittableRuntimeParams.TransmittableIECUnit unit : previousUnits) {
                itemEditors.add(unit.revertItemEditor());
            }
        }
        TransmittableRuntimeParams.TransmittableIECUnit[] currentUnits = currentTParams.itemEditorConfig();
        if (currentUnits != null) {
            for (TransmittableRuntimeParams.TransmittableIECUnit unit : currentUnits) {
                itemEditors.add(unit.applyToItemEditor());
            }
        }
        itemEditors.forEach(ItemEditor::apply);
    }
    public static TransmittableRuntimeParams getCurrentTParams() {
        return currentTParams;
    }
    public static LocalRuntimeParams getLocalRuntimeParams() {
        return localRuntimeParams;
    }
}
