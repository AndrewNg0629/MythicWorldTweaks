package top.aenp.mwt.mixin;

import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(SonicBoomTask.class)
public class SonicBoomTaskMixin {
    @Shadow
    @Final
    private static int RUN_TIME;

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"),
            method = "method_43265(Lnet/minecraft/entity/mob/WardenEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V",
            index = 1)
    private static float modifySoundDamage(float amount) {
        return (float) ConfigManager.getConfig().tweaks().valueTweaks().wardenSonicBoomControl().sonicBoomDamage();
    }

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addVelocity(DDD)V")
            , method = "method_43265(Lnet/minecraft/entity/mob/WardenEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V")
    private static void modifyKnockBackVelocity(Args args) {
        if (ConfigManager.getConfig().tweaks().valueTweaks().wardenSonicBoomControl().enabled()) {
            double xVelocity = args.get(0);
            double yVelocity = args.get(1);
            double zVelocity = args.get(2);
            double knockBackVelocityRate = ConfigManager.getConfig().tweaks().valueTweaks().wardenSonicBoomControl().sonicBoomKnockbackMultiplier();
            args.set(0, xVelocity * knockBackVelocityRate);
            args.set(1, yVelocity * knockBackVelocityRate);
            args.set(2, zVelocity * knockBackVelocityRate);
        }
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;remember(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/lang/Object;J)V"), index = 2, method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/WardenEntity;J)V")
    private long modifySoundInterval(long interval) {
        return RUN_TIME - ConfigManager.getConfig().tweaks().valueTweaks().wardenSonicBoomControl().sonicBoomIntervalTicks();
    }
}