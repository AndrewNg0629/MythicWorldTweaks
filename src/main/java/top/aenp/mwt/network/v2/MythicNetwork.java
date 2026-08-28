package top.aenp.mwt.network.v2;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.payloads.*;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginC2SPayload;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginS2CPayload;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class MythicNetwork {
    public static final MythicNetwork INSTANCE = new MythicNetwork();
    public static final int QUERY_ID = -2147483600;
    public static final ImmutableSet<String> ALL_MODS;
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MythicWorldTweaks.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
    public static final int PROTOCOL_VERSION = 3;

    static {
        ImmutableSet.Builder<String> modIdSetBuilder = ImmutableSet.builder();
        FabricLoader fabricLoader = FabricLoader.getInstance();
        fabricLoader.getAllMods().forEach(modContainer -> modIdSetBuilder.add(modContainer.getMetadata().getId()));
        ALL_MODS = modIdSetBuilder.build();
    }

    public final ConcurrentHashMap<Identifier, PacketCodec<PacketByteBuf, ? extends MythicLoginS2CPayload>> LOGIN_S2C_CODECS = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Identifier, PacketCodec<PacketByteBuf, ? extends MythicLoginC2SPayload>> LOGIN_C2S_CODECS = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Identifier, PacketCodec<PacketByteBuf, ? extends CustomPayload>> CUSTOM_PAYLOAD_CODECS = new ConcurrentHashMap<>();
    private boolean initialized = false;
    private volatile MinecraftServer currentServer = null;

    public void initialize() {
        if (!initialized) {
            LOGIN_S2C_CODECS.put(LoginModVersionS2CPayload.ID, LoginModVersionS2CPayload.CODEC);
            LOGIN_S2C_CODECS.put(NetworkSyncedConfig.ID, NetworkSyncedConfig.PACKET_CODEC);
            LOGIN_S2C_CODECS.put(LoginModIdRequestS2CPayload.ID, LoginModIdRequestS2CPayload.CODEC);
            LOGIN_C2S_CODECS.put(LoginModVersionC2SPayload.ID, LoginModVersionC2SPayload.CODEC);
            LOGIN_C2S_CODECS.put(LoginModIdListC2SPayload.ID, LoginModIdListC2SPayload.CODEC);

            CUSTOM_PAYLOAD_CODECS.put(NetworkSyncedConfig.ID, NetworkSyncedConfig.PACKET_CODEC);
            CUSTOM_PAYLOAD_CODECS.put(TrySleepC2SPayload.ID.id(), TrySleepC2SPayload.CODEC);
            CUSTOM_PAYLOAD_CODECS.put(SleepingStateUpdateS2CPayload.ID.id(), SleepingStateUpdateS2CPayload.CODEC);

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
