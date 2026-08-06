package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ServerPlayNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicPlayC2SPayload;

public class BedIdleSignalPayload implements MythicPlayC2SPayload {
    public static final CustomPayload.Id<BedIdleSignalPayload> ID = new Id<>(Identifier.of("mwt", "bed_idle_signal"));
    public static final PacketCodec<PacketByteBuf, BedIdleSignalPayload> CODEC = new PacketCodec<>() {
        @Override
        public BedIdleSignalPayload decode(PacketByteBuf buf) {
            return new BedIdleSignalPayload();
        }

        @Override
        public void encode(PacketByteBuf buf, BedIdleSignalPayload value) {
        }
    };

    @Override
    public void handle(ServerPlayNetworkHandlerMethodInjections handler) {
        handler.mythicworldtweaks$onBedIdleSignal();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
