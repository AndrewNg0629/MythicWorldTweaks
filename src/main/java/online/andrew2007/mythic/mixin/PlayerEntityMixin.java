package online.andrew2007.mythic.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.PlayerEntityStuff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V", shift = At.Shift.AFTER), method = "initDataTracker")
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo info) {
        builder.add(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION, false);
        builder.add(PlayerEntityStuff.IS_FAKE, PlayerEntityStuff.determineFake((PlayerEntity) (Object) this));
        builder.add(PlayerEntityStuff.IS_REALLY_SLEEPING, false);
    }

    @Inject(at = @At(value = "HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        PlayerEntity thisOBJ = (PlayerEntity) (Object) this;
        if (thisOBJ instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.getDataTracker().get(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION)) {
                serverPlayerEntity.getDataTracker().set(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION, false);
                info.setReturnValue(!RuntimeController.getCurrentTParams().playerRidingProtection());
            }
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z", ordinal = 0), method = "tick")
    private boolean sleepingTimerControl(PlayerEntity instance) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            return instance.isSleeping() && instance.getDataTracker().get(PlayerEntityStuff.IS_REALLY_SLEEPING);
        } else {
            return instance.isSleeping();
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z", ordinal = 0), method = "canResetTimeBySleeping")
    private boolean canReallySkipNight(PlayerEntity instance) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            return instance.isSleeping() && instance.getDataTracker().get(PlayerEntityStuff.IS_REALLY_SLEEPING);
        } else {
            return instance.isSleeping();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getXpToDrop", cancellable = true)
    private void getXpToDrop(CallbackInfoReturnable<Integer> info) {
        if (RuntimeController.getCurrentTParams().keepExperience()) {
            info.setReturnValue(0);
        }
    }
}
