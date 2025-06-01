package online.andrew2007.mythic.config;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.configFileParser.BinaryToggleTweaksConfig;
import online.andrew2007.mythic.config.configFileParser.ItemEditorConfig;
import online.andrew2007.mythic.config.configFileParser.ModConfig;
import online.andrew2007.mythic.config.configFileParser.ParamsRequiredTweaksConfig;
import online.andrew2007.mythic.config.runtimeParams.LocalRuntimeParams;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import online.andrew2007.mythic.item.ItemEditor;
import online.andrew2007.mythic.modFunctions.LocalToaster;
import online.andrew2007.mythic.modFunctions.WardenEntityStuff;
import online.andrew2007.mythic.network.payloads.PlayConfigPushPayload;

import java.util.ArrayList;
import java.util.HashSet;

public class RuntimeController {
    private static LocalRuntimeParams localRuntimeParams = LocalRuntimeParams.getDefaultInstance();
    private static boolean localRuntimeParamsInitialized = false;
    private static TransmittableRuntimeParams currentTParams = TransmittableRuntimeParams.getDefaultInstance();
    private static TransmittableRuntimeParams.TransmittableIECUnit[] previousItemEditorConfig = new TransmittableRuntimeParams.TransmittableIECUnit[0];
    private static TransmittableRuntimeParams serverPlayReceivedTParams = null;
    private static boolean isDuringMythicServerPlay = false;

    public static synchronized void loadTransmittableParamsFromConfig(boolean isStartup) {
        ModConfig modConfig = ConfigLoader.getCurrentModConfig();
        TransmittableRuntimeParams newParams;
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
                transmittableIECUnits = new TransmittableRuntimeParams.TransmittableIECUnit[0];
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
                    binaryToggleTweaksConfig.suicideCommand(),
                    binaryToggleTweaksConfig.keepExperience(),
                    binaryToggleTweaksConfig.alwaysDragonEgg(),
                    binaryToggleTweaksConfig.bowEnchantmentsForCrossbow(),
                    binaryToggleTweaksConfig.creativePlayerVoidResistance(),
                    paramsRequiredTweaksConfig.autoDiscardingFireBallConfig().enabled(),
                    paramsRequiredTweaksConfig.stuffedShulkerBoxStackLimitConfig().enabled(),
                    paramsRequiredTweaksConfig.shulkerBoxNestingLimitConfig().enabled(),
                    paramsRequiredTweaksConfig.wardenAttributesWeakeningConfig().enabled(),
                    paramsRequiredTweaksConfig.wardenSonicBoomWeakeningConfig().enabled(),
                    paramsRequiredTweaksConfig.playerDeathItemProtectionConfig().enabled(),
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
                    paramsRequiredTweaksConfig.playerDeathItemProtectionConfig().itemDiscardTicks(),
                    paramsRequiredTweaksConfig.playerDeathItemProtectionConfig().mobPickupProtection(),
                    paramsRequiredTweaksConfig.playerDeathItemProtectionConfig().strictPickup(),
                    itemEditorEnabled,
                    transmittableIECUnits
            );
        } else {
            newParams = TransmittableRuntimeParams.getDefaultInstance();
        }
        if (localRuntimeParams.modEnabled()) {
            currentTParams = newParams;
            if (!isDuringMythicServerPlay) {
                onParamsChange();
            }
            if (getLocalRuntimeParams().serverPlaySupportEnabled() && ConfigLoader.isDuringServerRuntime() && !isStartup) {
                MinecraftServer server = ConfigLoader.getCurrentServer();
                ArrayList<ServerPlayerEntity> playerList = new ArrayList<>(server.getPlayerManager().getPlayerList());
                String hostPlayerName;
                if (!server.isDedicated()) {
                    GameProfile hostProfile = server.getHostProfile();
                    if (hostProfile != null) {
                        hostPlayerName = hostProfile.getName();
                    } else {
                        hostPlayerName = null;
                    }
                } else {
                    hostPlayerName = null;
                }
                playerList.removeIf(player -> player.getGameProfile().getName().equalsIgnoreCase(hostPlayerName));
                for (ServerPlayerEntity player : playerList) {
                    ServerPlayNetworking.send(player, new PlayConfigPushPayload(getCurrentTParams()));
                    player.mythicWorldTweaks$onPlayConfigPush();
                }
            }
        }
    }

    public static void loadLocalParamsFromConfig() {
        if (!localRuntimeParamsInitialized) {
            try {
                ModConfig modConfig = ConfigLoader.getConfigObject();
                localRuntimeParams = new LocalRuntimeParams(
                        modConfig.modEnabled(),
                        modConfig.modDataPackEnabled(),
                        modConfig.serverPlaySupportEnabled(),
                        modConfig.serverName(),
                        modConfig.modIdValidationConfig().enabled(),
                        modConfig.modIdValidationConfig().modIdList()
                );
                localRuntimeParamsInitialized = true;
            } catch (RuntimeException e) {
                MythicWorldTweaks.LOGGER.error("Unable to load config on startup, Minecraft is shutting down.");
                throw e;
            }
        }
    }

    private static void onParamsChange() {
        applyItemEdits();
        WardenEntityStuff.modifyWardenAttributes();
        WardenEntityStuff.WardenEntityTrack.wardenRefresh();
    }

    private static void applyItemEdits() {
        HashSet<ItemEditor> itemEditors = new HashSet<>();
        if (previousItemEditorConfig != null) {
            for (TransmittableRuntimeParams.TransmittableIECUnit unit : previousItemEditorConfig) {
                itemEditors.add(unit.revertItemEditor());
            }
        }
        TransmittableRuntimeParams.TransmittableIECUnit[] currentUnits = getCurrentTParams().itemEditorConfig();
        if (currentUnits != null) {
            for (TransmittableRuntimeParams.TransmittableIECUnit unit : currentUnits) {
                itemEditors.add(unit.applyToItemEditor());
            }
        }
        itemEditors.forEach(ItemEditor::apply);
        previousItemEditorConfig = currentUnits;
    }

    public static synchronized void receiveConfigPush(TransmittableRuntimeParams params) {
        serverPlayReceivedTParams = params;
        isDuringMythicServerPlay = true;
        onParamsChange();
        LocalToaster.toast(Text.of("MythicWorldTweaks"), Text.translatable("mythicworldtweaks.network.received_config"));
        MythicWorldTweaks.LOGGER.info("Successfully received and applied mod config from the server.");
    }

    public synchronized static void exitMythicServerPlay() {
        if (isDuringMythicServerPlay) {
            isDuringMythicServerPlay = false;
            serverPlayReceivedTParams = null;
            onParamsChange();
            MythicWorldTweaks.LOGGER.info("Disconnected from server, switch from server config to local config.");
        }
    }

    public static TransmittableRuntimeParams getCurrentTParams() {
        return isDuringMythicServerPlay ? serverPlayReceivedTParams : currentTParams;
    }

    public static LocalRuntimeParams getLocalRuntimeParams() {
        return localRuntimeParams;
    }

    public static boolean isDuringMythicServerPlay() {
        return isDuringMythicServerPlay;
    }
}
