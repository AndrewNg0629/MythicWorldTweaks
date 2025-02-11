package online.andrew2007.mythic.network.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;

public record LoginConfigPushS2CPayload(TransmittableRuntimeParams params) implements LoginQueryRequestPayload {
    public static final Identifier payloadId = Identifier.of(MythicWorldTweaks.MOD_ID, "login_config_push");
    @Override
    public Identifier id() {
        return payloadId;
    }
    @Override
    public void write(PacketByteBuf buf) {
        String serialized = TransmittableRuntimeParams.TRANSMITTING_GSON.toJson(this.params);
        buf.writeString(serialized);
    }
}
