package top.aenp.mwt.config.v2;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.injections.ClientPlayNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.MythicLoginS2CPayload;
import top.aenp.mwt.network.v2.payloads.MythicPlayS2CPayload;

public record NetworkSyncedConfig(
        boolean largeFireCharge,
        boolean bedIdle,
        boolean suicideCommand,
        ModConfig.ValueTweaks.WardenAttributesControl wardenAttributesControl,
        ModConfig.ItemEditorConfig itemEditorConfig
) implements MythicLoginS2CPayload, MythicPlayS2CPayload {
    public static final PacketCodec<PacketByteBuf, NetworkSyncedConfig> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public NetworkSyncedConfig decode(PacketByteBuf buf) {
            boolean largeFireCharge = buf.readBoolean();
            boolean bedIdle = buf.readBoolean();
            boolean suicideCommand = buf.readBoolean();
            NbtElement wardenAttributesControlNBT = buf.readNbt();
            ModConfig.ValueTweaks.WardenAttributesControl wardenAttributesControl = ModConfig.ValueTweaks.WardenAttributesControl.CODEC.parse(NbtOps.INSTANCE, wardenAttributesControlNBT).getOrThrow();
            NbtElement itemEditorConfigNBT = buf.readNbt();
            ModConfig.ItemEditorConfig itemEditorConfig = ModConfig.ItemEditorConfig.CODEC.parse(NbtOps.INSTANCE, itemEditorConfigNBT).getOrThrow();
            return new NetworkSyncedConfig(largeFireCharge, bedIdle, suicideCommand, wardenAttributesControl, itemEditorConfig);
        }

        @Override
        public void encode(PacketByteBuf buf, NetworkSyncedConfig value) {
            NbtElement wardenAttributesControlNBT = ModConfig.ValueTweaks.WardenAttributesControl.CODEC.encodeStart(NbtOps.INSTANCE, value.wardenAttributesControl).getOrThrow();
            NbtElement itemEditorConfigNBT = ModConfig.ItemEditorConfig.CODEC.encodeStart(NbtOps.INSTANCE, value.itemEditorConfig).getOrThrow();
            buf.writeBoolean(value.largeFireCharge);
            buf.writeBoolean(value.bedIdle);
            buf.writeBoolean(value.suicideCommand);
            buf.writeNbt(wardenAttributesControlNBT);
            buf.writeNbt(itemEditorConfigNBT);
        }
    };

    public static final Identifier ID = Identifier.of("labmod", "network_synced_config"); //TODO
    public static final CustomPayload.Id<NetworkSyncedConfig> PAYLOAD_ID = new CustomPayload.Id<>(ID);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(ClientLoginNetworkHandlerMethodInjections handler) {
        handler.labmod$onConfigPush(this);
    }

    @Override
    public void handle(ClientPlayNetworkHandlerMethodInjections handler) {
        handler.labmod$onConfigPush(this);
    }
}
