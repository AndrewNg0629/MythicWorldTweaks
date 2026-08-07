package top.aenp.mwt.network.v2.payloads.interfaces;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;

@SuppressWarnings("unchecked")
public interface MythicLoginS2CPayload extends LoginQueryRequestPayload {
    @Override
    default Identifier id() {
        return Identifier.of("mwt", "login_s2c_common"); //No use.
    }

    @Override
    default void write(PacketByteBuf buf) {
        PacketCodec<PacketByteBuf, MythicLoginS2CPayload> codec = (PacketCodec<PacketByteBuf, MythicLoginS2CPayload>) MythicNetwork.INSTANCE.LOGIN_S2C_CODECS.get(mythicId());
        buf.writeIdentifier(mythicId());
        codec.encode(buf, this);
    }

    Identifier mythicId();

    void handle(ClientLoginNetworkHandlerMethodInjections handler);
}
