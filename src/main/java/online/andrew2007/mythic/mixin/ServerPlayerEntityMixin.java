package online.andrew2007.mythic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.util.PlayerEntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        ServerPlayerEntity thisOBJ = (ServerPlayerEntity) (Object) this;
        if (RuntimeController.getCurrentTParams().playerRidingGestures()) {
            PlayerEntityUtil.sneakingDCCheck(thisOBJ);
        }
        if (RuntimeController.getCurrentTParams().playerRidingProtection()) {
            if (thisOBJ.getDataTracker().get(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION)) {
                if ((thisOBJ.isOnGround() || thisOBJ.isInFluid() || thisOBJ.isFallFlying() || (thisOBJ.isCreative() && thisOBJ.getAbilities().flying))) {
                    thisOBJ.getDataTracker().set(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION, false);
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "onDeath")
    private void onDeath(DamageSource damageSource, CallbackInfo info) {
        ((ServerPlayerEntity) (Object) this).getDataTracker().set(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION, false);
    }

    @Inject(at = @At(value = "HEAD"), method = "onDisconnect")
    private void onDisconnect(CallbackInfo info) {
        ServerPlayerEntity thisOBJ = (ServerPlayerEntity) (Object) this;
        Entity passenger = thisOBJ.getFirstPassenger();
        if (RuntimeController.getCurrentTParams().playerRidingProtection()) {
            if (passenger != null) {
                if (passenger instanceof ServerPlayerEntity playerPassenger) {
                    playerPassenger.getDataTracker().set(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION, true);
                }
            }
        }
    }
}
