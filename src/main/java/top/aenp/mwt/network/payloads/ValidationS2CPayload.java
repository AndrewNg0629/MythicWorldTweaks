package top.aenp.mwt.network.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.MythicWorldTweaks;

@Deprecated(forRemoval = true)
public record ValidationS2CPayload(String serverName, String gameVersion,
                                   String modVersion) implements LoginQueryRequestPayload {
    public static final Identifier payloadId = Identifier.of(MythicWorldTweaks.MOD_ID, "login_validation");

    @Override
    public Identifier id() {
        return payloadId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(serverName);
        buf.writeString(gameVersion);
        buf.writeString(modVersion);
    }
}
