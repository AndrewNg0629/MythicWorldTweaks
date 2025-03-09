package online.andrew2007.mythic.mixin;

import net.minecraft.entity.mob.WardenEntity;
import online.andrew2007.mythic.modFunctions.WardenEntityStuff;
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
            WardenEntityStuff.WardenEntityTrack.registerEntity(thisOBJ);
        }
    }
}
