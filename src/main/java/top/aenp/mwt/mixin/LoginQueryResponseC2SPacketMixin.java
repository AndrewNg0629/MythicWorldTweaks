package top.aenp.mwt.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.config.runtimeParams.TransmittableRuntimeParams;
import top.aenp.mwt.network.MythicNetwork;
import top.aenp.mwt.network.payloads.LoginConfigPushC2SPayload;
import top.aenp.mwt.network.payloads.LoginConfigPushS2CPayload;
import top.aenp.mwt.network.payloads.ValidationC2SPayload;
import top.aenp.mwt.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LoginQueryResponseC2SPacket.class, priority = 999)
public abstract class LoginQueryResponseC2SPacketMixin {
    @Inject(at = @At(value = "HEAD"), method = "readPayload", cancellable = true)
    private static void readResponse(int queryId, PacketByteBuf buf, CallbackInfoReturnable<LoginQueryResponsePayload> info) {
        boolean hasPayload = buf.getBoolean(buf.readerIndex());
        if (hasPayload) {
            if (MythicNetwork.isMythicResponse(queryId)) {
                buf.readBoolean();
                Identifier payloadId = MythicNetwork.getResponseId(queryId);
                if (payloadId.equals(ValidationS2CPayload.payloadId)) {
                    String serialized = buf.readString();
                    info.setReturnValue(MythicNetwork.DEFAULT_GSON.fromJson(serialized, ValidationC2SPayload.class));
                } else if (payloadId.equals(LoginConfigPushS2CPayload.payloadId)) {
                    String serialized = buf.readString();
                    TransmittableRuntimeParams params = TransmittableRuntimeParams.TRANSMITTING_GSON.fromJson(serialized, TransmittableRuntimeParams.class);
                    info.setReturnValue(new LoginConfigPushC2SPayload(params));
                }
            }
        }
    }
}
