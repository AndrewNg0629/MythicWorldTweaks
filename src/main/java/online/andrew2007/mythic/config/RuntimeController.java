package online.andrew2007.mythic.config;

import online.andrew2007.mythic.config.configFileParser.BinaryToggleTweaksConfig;
import online.andrew2007.mythic.config.configFileParser.ItemEditorConfig;
import online.andrew2007.mythic.config.configFileParser.ModConfig;
import online.andrew2007.mythic.config.configFileParser.ParamsRequiredTweaksConfig;
import online.andrew2007.mythic.config.runtimeParams.LocalRuntimeParams;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import online.andrew2007.mythic.util.WardenEntityUtil;

public class RuntimeController {
    static boolean localParamsLoaded = false;
    private static LocalRuntimeParams localRuntimeParams = new LocalRuntimeParams();
    private static TransmittableRuntimeParams currentTParams = TransmittableRuntimeParams.getDefaultInstance();
    private static TransmittableRuntimeParams serverPlayCachedTParams = null;

    static void loadTransmittableParamsFromConfig(ModConfig modConfig) {
        TransmittableRuntimeParams.TransmittableIECUnit[] previousIRCUnits = currentTParams.itemEditorConfig();
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
        currentTParams = new TransmittableRuntimeParams(
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
        applyItemEdits(previousIRCUnits);
        WardenEntityUtil.modifyWardenAttributes();
    }
    private static void applyItemEdits(TransmittableRuntimeParams.TransmittableIECUnit[] previousUnits) {
        if (previousUnits != null) {
            for (TransmittableRuntimeParams.TransmittableIECUnit unit : previousUnits) {
                unit.revertItemEditor().apply();
            }
        }
        TransmittableRuntimeParams.TransmittableIECUnit[] currentUnits = currentTParams.itemEditorConfig();
        if (currentUnits != null) {
            for (TransmittableRuntimeParams.TransmittableIECUnit unit : currentUnits) {
                unit.applyToItemEditor().apply();
            }
        }
    }
    public static TransmittableRuntimeParams getCurrentTParams() {
        return currentTParams;
    }
}
