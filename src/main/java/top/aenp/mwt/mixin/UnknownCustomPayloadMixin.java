package top.aenp.mwt.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.network.v2.MythicNetwork;

@Mixin(UnknownCustomPayload.class)
public class UnknownCustomPayloadMixin {
    @Inject(method = "createCodec", at = @At(value = "HEAD"), cancellable = true)
    private static void supplyMythicCodec(Identifier id, int maxBytes, CallbackInfoReturnable<PacketCodec<PacketByteBuf, ? extends CustomPayload>> info) {
        PacketCodec<PacketByteBuf, ? extends CustomPayload> codec = MythicNetwork.CUSTOM_PAYLOAD_CODECS.get(id);
        if (codec != null) {
            info.setReturnValue(codec);
        }
    }
}
