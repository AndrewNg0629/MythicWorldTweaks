package online.andrew2007.data.mixin;

import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ServerMainMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;createGameVersion()V", shift = At.Shift.AFTER), method = "main")
    private static void clientMain(String[] args, CallbackInfo info) {
        try {
            Class.forName("online.andrew2007.mythic.MythicWorldTweaks");
        } catch (ClassNotFoundException e) {
            System.exit(0);
        }
    }
}
