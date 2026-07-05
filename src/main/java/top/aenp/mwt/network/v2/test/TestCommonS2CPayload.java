package top.aenp.mwt.network.v2.test;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ClientPlayNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.MythicPlayS2CPayload;

public record TestCommonS2CPayload(String data) implements MythicPlayS2CPayload {
    public static final CustomPayload.Id<TestCommonS2CPayload> ID = new CustomPayload.Id<>(Identifier.of("labmod", "test_common_s2c"));
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final PacketCodec<PacketByteBuf, TestCommonS2CPayload> CODEC = new PacketCodec<>() {
        @Override
        public TestCommonS2CPayload decode(PacketByteBuf buf) {
            return new TestCommonS2CPayload(buf.readString());
        }
        @Override
        public void encode(PacketByteBuf buf, TestCommonS2CPayload value) {
            buf.writeString(value.data);
        }
    };

    @Override
    public void handle(ClientPlayNetworkHandlerMethodInjections handler) {
        handler.labmod$onCustomS2C(this);
    }
}
