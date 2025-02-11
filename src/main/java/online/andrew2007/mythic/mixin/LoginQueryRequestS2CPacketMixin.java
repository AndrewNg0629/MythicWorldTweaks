package online.andrew2007.mythic.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import online.andrew2007.mythic.network.payloads.LoginConfigPushS2CPayload;
import online.andrew2007.mythic.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LoginQueryRequestS2CPacket.class, priority = 100)
public class LoginQueryRequestS2CPacketMixin {
    @Inject(at = @At("HEAD"), method = "readPayload", cancellable = true)
    private static void readPayload(Identifier id, PacketByteBuf buf, CallbackInfoReturnable<LoginQueryRequestPayload> info) {
        if (id.equals(ValidationS2CPayload.payloadId)) {
            info.setReturnValue(new ValidationS2CPayload(buf.readString(), buf.readString(), buf.readString()));
        } else if (id.equals(LoginConfigPushS2CPayload.payloadId)) {
            String serialized = buf.readString();
            TransmittableRuntimeParams params = TransmittableRuntimeParams.TRANSMITTING_GSON.fromJson(serialized, TransmittableRuntimeParams.class);
            info.setReturnValue(new LoginConfigPushS2CPayload(params));
        }
    }
}
