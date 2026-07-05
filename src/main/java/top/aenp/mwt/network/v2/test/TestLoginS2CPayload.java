package top.aenp.mwt.network.v2.test;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.MythicLoginS2CPayload;

public record TestLoginS2CPayload(String hello) implements MythicLoginS2CPayload {
    public static final Identifier ID = Identifier.of("labmod", "test_s2c");
    public static final PacketCodec<PacketByteBuf, TestLoginS2CPayload> CODEC = new PacketCodec<>() {
        @Override
        public TestLoginS2CPayload decode(PacketByteBuf buf) {
            return new TestLoginS2CPayload(buf.readString());
        }
        @Override
        public void encode(PacketByteBuf buf, TestLoginS2CPayload value) {
            buf.writeString(value.hello);
        }
    };

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(ClientLoginNetworkHandlerMethodInjections handler) {
        handler.labmod$onTestLoginS2C(this);
    }
}
