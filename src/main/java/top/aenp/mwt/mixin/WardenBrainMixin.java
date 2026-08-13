package top.aenp.mwt.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.mob.WardenBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.ArrayList;

@Mixin(WardenBrain.class)
public class WardenBrainMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/RangedApproachTask;create(F)Lnet/minecraft/entity/ai/brain/task/Task;"), index = 0, method = "addFightActivities")
    private static float modifyApproachSpeed(float speed) {
        return (float) ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().chasingMovementSpeed();
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/MeleeAttackTask;create(I)Lnet/minecraft/entity/ai/brain/task/SingleTickTask;"), index = 0, method = "addFightActivities")
    private static int modifyMAttackInterval(int interval) {
        return ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().attackIntervalTicks();
    }

    @ModifyArg(method = "addFightActivities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;ILcom/google/common/collect/ImmutableList;Lnet/minecraft/entity/ai/brain/MemoryModuleType;)V"), index = 2)
    private static <E> ImmutableList<E> editTasks(ImmutableList<E> immutableTasks) {
        if (!ConfigManager.getConfig().tweaks().valueTweaks().wardenSonicBoomControl().sonicBoomEnabled()) {
            ArrayList<E> tasks = new ArrayList<>(immutableTasks);
            tasks.removeIf(task -> task instanceof SonicBoomTask);
            return ImmutableList.copyOf(tasks);
        } else {
            return immutableTasks;
        }
    }
}