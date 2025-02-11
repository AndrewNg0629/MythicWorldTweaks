package online.andrew2007.mythic.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(at = @At(value = "TAIL"), method = "disconnect(Lnet/minecraft/network/DisconnectionInfo;)V")
    private void disconnect(DisconnectionInfo disconnectionInfo, CallbackInfo info) {
        RuntimeController.exitMythicServerPlay();
    }
}
