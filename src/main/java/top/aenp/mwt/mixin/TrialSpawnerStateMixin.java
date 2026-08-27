package top.aenp.mwt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(TrialSpawnerState.class)
public class TrialSpawnerStateMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/spawner/TrialSpawnerData;areMobsDead()Z"))
    private boolean wrapAreMobsDead(TrialSpawnerData instance, Operation<Boolean> original, @Local(argsOnly = true) TrialSpawnerLogic logic) {
        boolean allDead = original.call(instance);
        if (ConfigManager.getConfig().tweaks().valueTweaks().trialSpawnerCooldownOverride().enabled() && allDead) {
            logic.getData().mythicworldtweaks$setCompletionElapsedTicks(0);
        }
        return allDead;
    }
}
