package top.aenp.mwt.network.v2.test;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ServerLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.MythicLoginC2SPayload;

public record TestLoginC2SPayload(String hello) implements MythicLoginC2SPayload {
    public static final Identifier ID = Identifier.of("labmod", "test_c2s");
    public static final PacketCodec<PacketByteBuf, TestLoginC2SPayload> CODEC = new PacketCodec<>() {
        @Override
        public TestLoginC2SPayload decode(PacketByteBuf buf) {
            return new TestLoginC2SPayload(buf.readString());
        }
        @Override
        public void encode(PacketByteBuf buf, TestLoginC2SPayload value) {
            buf.writeString(value.hello());
        }
    };
    public TestLoginC2SPayload(PacketByteBuf buf) {
        this(buf.readString());
    }

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(ServerLoginNetworkHandlerMethodInjections handler) {
        handler.labmod$onTestLoginC2S(this);
    }
}
