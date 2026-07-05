package top.aenp.mwt.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.network.v2.MythicNetwork;

@Mixin(value = ClientConnection.class, priority = 990)
public abstract class ClientConnectionMixin {
    @Shadow
    private int packetsSentCounter;

    @Shadow
    private Channel channel;

    @Shadow
    protected abstract void sendInternal(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush);

    @SuppressWarnings("resource")
    @Inject(method = "sendImmediately", at = @At(value = "HEAD"), cancellable = true)
    private void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo info) {
        if (packet instanceof LoginQueryRequestS2CPacket queryPacket) {
            if (queryPacket.queryId() == MythicNetwork.QUERY_ID) {
                this.packetsSentCounter++;
                if (this.channel.eventLoop().inEventLoop()) {
                    this.sendInternal(packet, callbacks, flush);
                } else {
                    this.channel.eventLoop().execute(() -> this.sendInternal(packet, callbacks, flush));
                }
                info.cancel();
            }
        }
    }
}
