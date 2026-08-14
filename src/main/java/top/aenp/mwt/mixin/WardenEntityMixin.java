package top.aenp.mwt.mixin;

import net.minecraft.entity.mob.WardenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.misc.WardenEntityStuff;

@SuppressWarnings("DataFlowIssue")
@Mixin(WardenEntity.class)
public abstract class WardenEntityMixin {
    @Inject(at = @At(value = "RETURN"), method = "<init>")
    private void WardenEntity(CallbackInfo info) {
        if (!((WardenEntity) (Object) this).getWorld().isClient()) {
            WardenEntity thisOBJ = (WardenEntity) (Object) this;
            WardenEntityStuff.WardenEntityTracker.INSTANCE.registerEntity(thisOBJ);
        }
    }
}
