package online.andrew2007.mythic.mixin;

import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.network.payloads.LoginConfigPushS2CPayload;
import online.andrew2007.mythic.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ServerLoginNetworkAddon.class)
public class ServerLoginNetworkAddonMixin {
    @Inject(at = @At(value = "HEAD"), method = "registerOutgoingPacket", cancellable = true)
    private void registerOutgoingPacket(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
        Identifier payloadId = packet.payload().id();
        if (Set.of(ValidationS2CPayload.payloadId, LoginConfigPushS2CPayload.payloadId).contains(payloadId)) {
            info.cancel();
        }
    }
}
