package online.andrew2007.mythic.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.mob.WardenBrain;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WardenBrain.class)
public class WardenBrainMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/RangedApproachTask;create(F)Lnet/minecraft/entity/ai/brain/task/Task;"), index = 0, method = "addFightActivities")
    private static float modifyApproachSpeed(float speed) {
        return RuntimeController.getCurrentTParams().wardenAttributesWeakeningEnabled() ? RuntimeController.getCurrentTParams().wardenChasingMovementSpeed() : speed;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/MeleeAttackTask;create(I)Lnet/minecraft/entity/ai/brain/task/SingleTickTask;"), index = 0, method = "addFightActivities")
    private static int modifyMAttackInterval(int interval) {
        return RuntimeController.getCurrentTParams().wardenAttributesWeakeningEnabled() ? RuntimeController.getCurrentTParams().wardenAttackIntervalTicks() : interval;
    }

    @Redirect(at = @At(value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;",
            remap = false),
            method = "addFightActivities")
    private static <E> ImmutableList<E> editTasks(E e1, E e2, E e3, E e4, E e5, E e6) {
        return RuntimeController.getCurrentTParams().silentWardens() ? ImmutableList.of(e1, e2, e3, e4, e6) : ImmutableList.of(e1, e2, e3, e4, e5, e6);
    }
}