package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.injections.ServerLoginNetworkHandlerMethodInjections;


@SuppressWarnings("unchecked")
public interface MythicLoginC2SPayload extends LoginQueryResponsePayload {
    @Override
    default void write(PacketByteBuf buf) {
        PacketCodec<PacketByteBuf, MythicLoginC2SPayload> codec = (PacketCodec<PacketByteBuf, MythicLoginC2SPayload>) MythicNetwork.LOGIN_C2S_CODECS.get(this.mythicId());
        buf.writeIdentifier(this.mythicId());
        codec.encode(buf, this);
    }

    Identifier mythicId();

    void handle(ServerLoginNetworkHandlerMethodInjections handler);
}
