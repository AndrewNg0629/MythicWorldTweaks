package top.aenp.mwt.mixin;

import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;

@Mixin(TrialSpawnerLogic.class)
public class TrialSpawnerLogicMixin {
    @Shadow
    @Final
    private TrialSpawnerData data;

    @Inject(method = "tickServer", at = @At(value = "HEAD"))
    private void tick(ServerWorld world, BlockPos pos, boolean ominous, CallbackInfo info) {
        ModConfig.Tweaks.ValueTweaks.TrialSpawnerCooldownOverride trialSpawnerCooldownOverride = ConfigManager.getConfig().tweaks().valueTweaks().trialSpawnerCooldownOverride();
        if (trialSpawnerCooldownOverride.enabled()) {
            int elapsedTicks = data.mythicworldtweaks$getCompletionElapsedTicks();
            if (elapsedTicks >= 0) {
                if (++elapsedTicks >= trialSpawnerCooldownOverride.cooldown() * 20) {
                    elapsedTicks = -1;
                }
            }
            data.mythicworldtweaks$setCompletionElapsedTicks(elapsedTicks);
        }
    }
}
