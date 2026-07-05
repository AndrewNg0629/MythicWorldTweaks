package top.aenp.mwt.network.v2.test;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ServerPlayNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.MythicPlayC2SPayload;

public record TestPlayC2SPayload(String data) implements MythicPlayC2SPayload {
    public static final CustomPayload.Id<TestPlayC2SPayload> ID = new CustomPayload.Id<>(Identifier.of("labmod", "test_playc2s"));
    public static final PacketCodec<PacketByteBuf, TestPlayC2SPayload> CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, TestPlayC2SPayload value) {
            buf.writeString(value.data);
        }
        @Override
        public TestPlayC2SPayload decode(PacketByteBuf buf) {
            return new TestPlayC2SPayload(buf.readString());
        }
    };

    @Override
    public void handle(ServerPlayNetworkHandlerMethodInjections handler) {
        handler.labmod$onCustomC2S(this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
