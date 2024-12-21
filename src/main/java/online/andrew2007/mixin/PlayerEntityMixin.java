package online.andrew2007.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import online.andrew2007.util.MiscUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V"), method = "initDataTracker")
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo info) {
        builder.add(MiscUtil.IS_UNDER_FALL_PROTECTION, false); //TODO Toggleable
    }

    @Inject(at = @At(value = "HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        PlayerEntity thisOBJ = (PlayerEntity) (Object) this;
        if (thisOBJ.getDataTracker().get(MiscUtil.IS_UNDER_FALL_PROTECTION)) {
            thisOBJ.getDataTracker().set(MiscUtil.IS_UNDER_FALL_PROTECTION, false);
            info.setReturnValue(false); //TODO
        }
    }
}
