package online.andrew2007.mixin;

import net.minecraft.entity.mob.WardenEntity;
import online.andrew2007.util.WardenEntityTrack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(WardenEntity.class)
public abstract class WardenEntityMixin {
    @Inject(at = @At(value = "TAIL"), method = "<init>")
    private void WardenEntity(CallbackInfo info) {
        if (!((WardenEntity) (Object) this).getWorld().isClient()) {
            WardenEntity thisOBJ = (WardenEntity) (Object) this;
            WardenEntityTrack.registerEntity(thisOBJ);/*
            for (Integer i1 : thisOBJ.getBrain().tasks.keySet()) {
                MythicWorldTweaks.LOGGER.info("++++++++++sub-map++++++++++");
                MythicWorldTweaks.LOGGER.info("Index: {}", i1);
                Map<Activity, Set<Task<? super WardenEntity>>> i2 = thisOBJ.getBrain().tasks.get(i1);
                for (Activity i3 : i2.keySet()) {
                    MythicWorldTweaks.LOGGER.info("Sub-index: {}", i3);
                    Set<Task<? super WardenEntity>> i4 = i2.get(i3);
                    for (Task<?> i5 : i4) {
                        MythicWorldTweaks.LOGGER.info(i5.toString());
                    }
                }
            }*/
        }
    }
}
