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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.aenp.mwt.misc.ReflectionUtils;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginC2SPayload;

@Mixin(value = LoginQueryResponseC2SPacket.class, priority = 990)
public class LoginQueryResponseC2SPacketMixin {
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
                        PacketCodec<PacketByteBuf, ? extends MythicLoginC2SPayload> codec = MythicNetwork.INSTANCE.LOGIN_C2S_CODECS.get(mythicType);
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
