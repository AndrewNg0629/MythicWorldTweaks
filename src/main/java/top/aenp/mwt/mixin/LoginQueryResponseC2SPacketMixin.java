package top.aenp.mwt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.config.runtimeParams.TransmittableRuntimeParams;

import top.aenp.mwt.misc.ReflectionUtils;
import top.aenp.mwt.network.payloads.LoginConfigPushC2SPayload;
import top.aenp.mwt.network.payloads.LoginConfigPushS2CPayload;
import top.aenp.mwt.network.payloads.ValidationC2SPayload;
import top.aenp.mwt.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.payloads.MythicLoginC2SPayload;

@Mixin(value = LoginQueryResponseC2SPacket.class, priority = 990)
public abstract class LoginQueryResponseC2SPacketMixin {
    @Inject(at = @At(value = "HEAD"), method = "readPayload", cancellable = true)
    private static void readResponse(int queryId, PacketByteBuf buf, CallbackInfoReturnable<LoginQueryResponsePayload> info) { //TODO Remove
        boolean hasPayload = buf.getBoolean(buf.readerIndex());
        if (hasPayload) {
            if (top.aenp.mwt.network.MythicNetwork.isMythicResponse(queryId)) {
                buf.readBoolean();
                Identifier payloadId = top.aenp.mwt.network.MythicNetwork.getResponseId(queryId);
                if (payloadId.equals(ValidationS2CPayload.payloadId)) {
                    String serialized = buf.readString();
                    info.setReturnValue(top.aenp.mwt.network.MythicNetwork.DEFAULT_GSON.fromJson(serialized, ValidationC2SPayload.class));
                } else if (payloadId.equals(LoginConfigPushS2CPayload.payloadId)) {
                    String serialized = buf.readString();
                    TransmittableRuntimeParams params = TransmittableRuntimeParams.TRANSMITTING_GSON.fromJson(serialized, TransmittableRuntimeParams.class);
                    info.setReturnValue(new LoginConfigPushC2SPayload(params));
                }
            }
        }
    }

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;createCodec(Lnet/minecraft/network/codec/ValueFirstEncoder;Lnet/minecraft/network/codec/PacketDecoder;)Lnet/minecraft/network/codec/PacketCodec;"))
    private static PacketCodec<PacketByteBuf, LoginQueryResponseC2SPacket> swapCodec(ValueFirstEncoder<PacketByteBuf, LoginQueryResponseC2SPacket> encoder, PacketDecoder<PacketByteBuf, LoginQueryResponseC2SPacket> decoder, Operation<PacketCodec<PacketByteBuf, LoginQueryResponseC2SPacket>> original) {
        return new PacketCodec<>() {
            @Override
            public LoginQueryResponseC2SPacket decode(PacketByteBuf buf) {
                int queryId = buf.readVarInt();
                LoginQueryResponsePayload payload = null;
                if (queryId == MythicNetwork.QUERY_ID) {
                    if (buf.readBoolean()) {
                        Identifier mythicType = buf.readIdentifier();
                        PacketCodec<PacketByteBuf, ? extends MythicLoginC2SPayload> codec = MythicNetwork.LOGIN_C2S_CODECS.get(mythicType);
                        payload = codec != null ? codec.decode(buf) : null;
                    }
                } else {
                    payload = ReflectionUtils.LoginQueryResponseC2SPacket$readPayload(queryId, buf);
                }
                return new LoginQueryResponseC2SPacket(queryId, payload);
            }

            @Override
            public void encode(PacketByteBuf buf, LoginQueryResponseC2SPacket packet) {
                buf.writeVarInt(packet.queryId());
                buf.writeNullable(packet.response(), (bufx, response) -> response.write(bufx));
            }
        };
    }
}
