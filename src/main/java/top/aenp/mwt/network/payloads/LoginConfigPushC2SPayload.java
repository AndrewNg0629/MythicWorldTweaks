package top.aenp.mwt.network.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;
import top.aenp.mwt.config.runtimeParams.TransmittableRuntimeParams;

@Deprecated(forRemoval = true)
public record LoginConfigPushC2SPayload(TransmittableRuntimeParams params) implements LoginQueryResponsePayload {
    @Override
    public void write(PacketByteBuf buf) {
        String serialized = TransmittableRuntimeParams.TRANSMITTING_GSON.toJson(this.params);
        buf.writeString(serialized);
    }
}
