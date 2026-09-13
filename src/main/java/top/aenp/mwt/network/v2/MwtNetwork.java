package top.aenp.mwt.network.v2;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import top.aenp.mwl.network.v2.MythicNetwork;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.payloads.*;

import java.util.LinkedList;

public class MwtNetwork {
    public static final MwtNetwork INSTANCE = new MwtNetwork();
    public static final ImmutableSet<String> ALL_MODS;
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MythicWorldTweaks.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
    public static final int PROTOCOL_VERSION = 3;

    static {
        ImmutableSet.Builder<String> modIdSetBuilder = ImmutableSet.builder();
        FabricLoader fabricLoader = FabricLoader.getInstance();
        fabricLoader.getAllMods().forEach(modContainer -> modIdSetBuilder.add(modContainer.getMetadata().getId()));
        ALL_MODS = modIdSetBuilder.build();
    }

    private boolean initialized = false;
    private volatile MinecraftServer currentServer = null;

    public void initialize() {
        if (!initialized) {
            MythicNetwork.INSTANCE.LOGIN_S2C_CODECS.put(LoginModVersionS2CPayload.ID, LoginModVersionS2CPayload.CODEC);
            MythicNetwork.INSTANCE.LOGIN_S2C_CODECS.put(NetworkSyncedConfig.ID, NetworkSyncedConfig.PACKET_CODEC);
            MythicNetwork.INSTANCE.LOGIN_S2C_CODECS.put(LoginModIdRequestS2CPayload.ID, LoginModIdRequestS2CPayload.CODEC);
            MythicNetwork.INSTANCE.LOGIN_C2S_CODECS.put(LoginModVersionC2SPayload.ID, LoginModVersionC2SPayload.CODEC);
            MythicNetwork.INSTANCE.LOGIN_C2S_CODECS.put(LoginModIdListC2SPayload.ID, LoginModIdListC2SPayload.CODEC);

            MythicNetwork.INSTANCE.CUSTOM_PAYLOAD_CODECS.put(NetworkSyncedConfig.ID, NetworkSyncedConfig.PACKET_CODEC);
            MythicNetwork.INSTANCE.CUSTOM_PAYLOAD_CODECS.put(TrySleepC2SPayload.ID.id(), TrySleepC2SPayload.CODEC);
            MythicNetwork.INSTANCE.CUSTOM_PAYLOAD_CODECS.put(SleepingStateUpdateS2CPayload.ID.id(), SleepingStateUpdateS2CPayload.CODEC);

            ServerLifecycleEvents.SERVER_STARTED.register(server -> currentServer = server);
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> currentServer = null);

            initialized = true;
        } else {
            throw new IllegalStateException();
        }
    }

    public void pushConfigDuringPlay() {
        if (currentServer != null) {
            LinkedList<ServerPlayerEntity> players = new LinkedList<>(currentServer.getPlayerManager().getPlayerList());
            String hostPlayerName;
            if (!currentServer.isDedicated()) {
                GameProfile hostProfile = currentServer.getHostProfile();
                if (hostProfile != null) {
                    hostPlayerName = hostProfile.getName();
                } else {
                    hostPlayerName = null;
                }
            } else {
                hostPlayerName = null;
            }
            players.removeIf(player -> player.getGameProfile().getName().equalsIgnoreCase(hostPlayerName));
            for (ServerPlayerEntity player : players) {
                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(ConfigManager.getInstance().getConfigToSend()));
            }
        }
    }

    public enum NegotiationStates {
        VERSION_S2C, VERSION_C2S, MOD_LIST
    }
}
