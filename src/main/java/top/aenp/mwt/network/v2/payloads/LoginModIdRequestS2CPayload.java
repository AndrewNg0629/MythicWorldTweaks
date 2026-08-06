package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginS2CPayload;

public class LoginModIdRequestS2CPayload implements MythicLoginS2CPayload {
    public static final Identifier ID = Identifier.of("mwt", "mod_id_req_s2c");
    public static final PacketCodec<PacketByteBuf, LoginModIdRequestS2CPayload> CODEC = new PacketCodec<>() {
        @Override
        public LoginModIdRequestS2CPayload decode(PacketByteBuf buf) {
            return new LoginModIdRequestS2CPayload();
        }

        @Override
        public void encode(PacketByteBuf buf, LoginModIdRequestS2CPayload value) {
        }
    };

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(ClientLoginNetworkHandlerMethodInjections handler) {
        handler.mythicworldtweaks$onModIdRequest();
    }
}
