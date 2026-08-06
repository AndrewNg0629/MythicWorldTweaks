package top.aenp.mwt.mixin.client;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handleDisconnection", at = @At(value = "HEAD"))
    private void handleDisconnection(CallbackInfo info) {
        ConfigManager.getInstance().exitMythicServerPlay();
    }
}
