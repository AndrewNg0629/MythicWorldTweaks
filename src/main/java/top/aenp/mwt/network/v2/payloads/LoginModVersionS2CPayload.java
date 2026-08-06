package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginS2CPayload;

public record LoginModVersionS2CPayload(String modVersion) implements MythicLoginS2CPayload {
    public static final Identifier ID = Identifier.of("mwt", "mod_version_s2c");
    public static final PacketCodec<PacketByteBuf, LoginModVersionS2CPayload> CODEC = new PacketCodec<>() {
        @Override
        public LoginModVersionS2CPayload decode(PacketByteBuf buf) {
            return new LoginModVersionS2CPayload(buf.readString());
        }

        @Override
        public void encode(PacketByteBuf buf, LoginModVersionS2CPayload value) {
            buf.writeString(value.modVersion);
        }
    };

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(ClientLoginNetworkHandlerMethodInjections handler) {
        handler.mythicworldtweaks$onModVersion(this);
    }
}
