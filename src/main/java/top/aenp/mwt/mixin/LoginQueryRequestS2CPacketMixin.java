package top.aenp.mwt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.aenp.mwt.misc.ReflectionUtils;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginS2CPayload;

@Mixin(value = LoginQueryRequestS2CPacket.class, priority = 990)
public class LoginQueryRequestS2CPacketMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;createCodec(Lnet/minecraft/network/codec/ValueFirstEncoder;Lnet/minecraft/network/codec/PacketDecoder;)Lnet/minecraft/network/codec/PacketCodec;"))
    private static PacketCodec<PacketByteBuf, LoginQueryRequestS2CPacket> swapCodec(ValueFirstEncoder<PacketByteBuf, LoginQueryRequestS2CPacket> encoder, PacketDecoder<PacketByteBuf, LoginQueryRequestS2CPacket> decoder, Operation<PacketCodec<PacketByteBuf, LoginQueryRequestS2CPacket>> original) {
        return new PacketCodec<>() {
            @Override
            public LoginQueryRequestS2CPacket decode(PacketByteBuf buf) {
                int queryId = buf.readVarInt();
                LoginQueryRequestPayload payload;
                if (queryId == MythicNetwork.QUERY_ID) {
                    buf.readIdentifier(); // Skip vanilla Identifier.
                    Identifier mythicType = buf.readIdentifier();
                    PacketCodec<PacketByteBuf, ? extends MythicLoginS2CPayload> codec = MythicNetwork.INSTANCE.LOGIN_S2C_CODECS.get(mythicType);
                    payload = codec != null ? codec.decode(buf) : null;
                } else {
                    payload = ReflectionUtils.LoginQueryRequestS2CPacket$readPayload(buf.readIdentifier(), buf);
                }
                return new LoginQueryRequestS2CPacket(queryId, payload);
            }

            @Override
            public void encode(PacketByteBuf buf, LoginQueryRequestS2CPacket packet) {
                buf.writeVarInt(packet.queryId());
                buf.writeIdentifier(packet.payload().id());
                packet.payload().write(buf);
            }
        };
    }
}
