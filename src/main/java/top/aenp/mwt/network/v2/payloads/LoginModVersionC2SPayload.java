package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.injections.ServerLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginC2SPayload;

public record LoginModVersionC2SPayload(String modVersion, int protocolVersion) implements MythicLoginC2SPayload {
    public static final Identifier ID = Identifier.of("mwt", "mod_version_c2s");
    public static final PacketCodec<PacketByteBuf, LoginModVersionC2SPayload> CODEC = new PacketCodec<>() {
        @Override
        public LoginModVersionC2SPayload decode(PacketByteBuf buf) {
            String modVersion = buf.readString();
            int protocolVersion = buf.readInt();
            return new LoginModVersionC2SPayload(modVersion, protocolVersion);
        }

        @Override
        public void encode(PacketByteBuf buf, LoginModVersionC2SPayload value) {
            buf.writeString(value.modVersion);
            buf.writeInt(value.protocolVersion);
        }
    };

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(ServerLoginNetworkHandlerMethodInjections handler) {
        handler.mythicworldtweaks$onModVersion(this);
    }
}
