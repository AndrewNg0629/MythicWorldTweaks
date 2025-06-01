package online.andrew2007.mythic.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.injectedInterfaces.PlayerEntityMethodInjections;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SleepManager.class)
public class SleepManagerMixin {
    @Inject(at = @At(value = "HEAD"), method = "update")
    private void update(List<ServerPlayerEntity> players, CallbackInfoReturnable<Boolean> info) {
        if (RuntimeController.getCurrentTParams().fakePlayerSleepExclusion()) {
            players.removeIf(PlayerEntityMethodInjections::mythicWorldTweaks$isFake);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSleeping()Z"), method = "update")
    private boolean isReallySleeping(ServerPlayerEntity instance) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            return instance.isSleeping() && instance.mythicWorldTweaks$isReallySleeping();
        } else {
            return instance.isSleeping();
        }
    }
}
