package top.aenp.mwt.network.v2.payloads;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;
import top.aenp.mwt.network.v2.interfaces.MythicClientLoginNetworkHandler;
import top.aenp.mwt.network.v2.interfaces.MythicClientPlayNetworkHandler;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginS2CPayload;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicPlayS2CPayload;

public record NetworkSyncedConfig(
        ModConfig.Tweaks.SyncedToggleTweaks1 syncedToggleTweaks1,
        ModConfig.Tweaks.ValueTweaks.WardenAttributesControl wardenAttributesControl,
        ModConfig.ItemEditorConfig itemEditorConfig
) implements MythicLoginS2CPayload, MythicPlayS2CPayload {
    public static final Codec<NetworkSyncedConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ModConfig.Tweaks.SyncedToggleTweaks1.CODEC.fieldOf("synced_toggle_tweaks1").forGetter(NetworkSyncedConfig::syncedToggleTweaks1),
                    ModConfig.Tweaks.ValueTweaks.WardenAttributesControl.CODEC.fieldOf("warden_attributes_control").forGetter(NetworkSyncedConfig::wardenAttributesControl),
                    ModConfig.ItemEditorConfig.CODEC.fieldOf("item_editor_config").forGetter(NetworkSyncedConfig::itemEditorConfig)
            ).apply(instance, NetworkSyncedConfig::new)
    );

    public static final PacketCodec<PacketByteBuf, NetworkSyncedConfig> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public NetworkSyncedConfig decode(PacketByteBuf buf) {
            NbtElement nbt = buf.readNbt();
            return NetworkSyncedConfig.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
        }

        @Override
        public void encode(PacketByteBuf buf, NetworkSyncedConfig value) {
            NbtElement nbt = NetworkSyncedConfig.CODEC.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
            buf.writeNbt(nbt);
        }
    };

    public static final Identifier ID = Identifier.of("mwt", "network_synced_config");
    public static final CustomPayload.Id<NetworkSyncedConfig> PAYLOAD_ID = new Id<>(ID);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(MythicClientLoginNetworkHandler handler) {
        ConfigManager.getInstance().onConfigPush(this);
    }

    @Override
    public void handle(MythicClientPlayNetworkHandler handler) {
        ConfigManager.getInstance().onConfigPush(this);
    }
}
