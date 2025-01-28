package online.andrew2007.mythic.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.util.PlayerEntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V"), method = "initDataTracker")
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo info) {
        if (((PlayerEntity) (Object) this) instanceof ServerPlayerEntity serverPlayerEntity) {
            builder.add(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION, false);
            builder.add(PlayerEntityUtil.IS_FAKE, PlayerEntityUtil.determineFake(serverPlayerEntity));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        PlayerEntity thisOBJ = (PlayerEntity) (Object) this;
        if (thisOBJ instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.getDataTracker().get(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION)) {
                serverPlayerEntity.getDataTracker().set(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION, false);
                info.setReturnValue(!RuntimeController.getCurrentTParams().playerRidingProtection());
            }
        }
    }
}
