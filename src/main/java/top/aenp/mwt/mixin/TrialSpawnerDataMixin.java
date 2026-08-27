package top.aenp.mwt.mixin;

import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.injected.interfaces.TrialSpawnerDataMethodInjections;

@Mixin(TrialSpawnerData.class)
public class TrialSpawnerDataMixin implements TrialSpawnerDataMethodInjections {
    @Unique
    long completionTimestamp = -1L;

    @Inject(method = "reset", at = @At(value = "HEAD"))
    private void reset(CallbackInfo info) {
        completionTimestamp = -1;
    }

    @Inject(method = "isCooldownOver", at = @At(value = "RETURN"), cancellable = true)
    private void isCooldownOver(ServerWorld world, CallbackInfoReturnable<Boolean> info) {
        if (ConfigManager.getConfig().tweaks().valueTweaks().trialSpawnerCooldownOverride().enabled()) {
            info.setReturnValue(completionTimestamp == -1);
        }
    }

    @Override
    public long mythicworldtweaks$getCompletionTimestamp() {
        return completionTimestamp;
    }

    @Override
    public void mythicworldtweaks$setCompletionTimestamp(long time) {
        completionTimestamp = time;
    }
}
