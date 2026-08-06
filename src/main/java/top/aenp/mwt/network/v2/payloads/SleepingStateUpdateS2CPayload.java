package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ClientPlayNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicPlayS2CPayload;

public record SleepingStateUpdateS2CPayload(boolean isReallySleeping) implements MythicPlayS2CPayload {
    public static final Id<SleepingStateUpdateS2CPayload> ID = new Id<>(Identifier.of("mwt", "sleeping_update"));
    public static final PacketCodec<PacketByteBuf, SleepingStateUpdateS2CPayload> CODEC = new PacketCodec<>() {
        @Override
        public SleepingStateUpdateS2CPayload decode(PacketByteBuf buf) {
            return new SleepingStateUpdateS2CPayload(buf.readBoolean());
        }

        @Override
        public void encode(PacketByteBuf buf, SleepingStateUpdateS2CPayload value) {
            buf.writeBoolean(value.isReallySleeping);
        }
    };
    @Override
    public void handle(ClientPlayNetworkHandlerMethodInjections handler) {
        handler.mythicworldtweaks$onSleepingStateUpdate(this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
