package online.andrew2007.mythic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(at = @At(value = "HEAD"), method = "tickInVoid", cancellable = true)
    private void tickInVoid(CallbackInfo info) {
        Entity thisOBJ = ((Entity) (Object) this);
        if (RuntimeController.getCurrentTParams().creativePlayerVoidResistance() && thisOBJ instanceof PlayerEntity player) {
            if (player.isInCreativeMode()) {
                info.cancel();
            }
        }
    }
}
