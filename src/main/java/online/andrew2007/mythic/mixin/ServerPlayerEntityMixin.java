package online.andrew2007.mythic.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.PlayerEntityStuff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        ServerPlayerEntity thisOBJ = (ServerPlayerEntity) (Object) this;
        if (RuntimeController.getCurrentTParams().playerRidingGestures()) {
            PlayerEntityStuff.sneakingDCCheck(thisOBJ);
        }
        if (RuntimeController.getCurrentTParams().playerRidingProtection()) {
            if (thisOBJ.getDataTracker().get(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION)) {
                if ((thisOBJ.isOnGround() || thisOBJ.isInFluid() || thisOBJ.isFallFlying() || (thisOBJ.isCreative() && thisOBJ.getAbilities().flying))) {
                    thisOBJ.getDataTracker().set(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION, false);
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "onDeath")
    private void onDeath(DamageSource damageSource, CallbackInfo info) {
        this.getDataTracker().set(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION, false);
        this.getDataTracker().set(PlayerEntityStuff.IS_REALLY_SLEEPING, false);
    }

    @Inject(at = @At(value = "HEAD"), method = "onDisconnect")
    private void onDisconnect(CallbackInfo info) {
        ServerPlayerEntity thisOBJ = (ServerPlayerEntity) (Object) this;
        Entity passenger = thisOBJ.getFirstPassenger();
        if (RuntimeController.getCurrentTParams().playerRidingProtection()) {
            if (passenger != null) {
                if (passenger instanceof ServerPlayerEntity playerPassenger) {
                    playerPassenger.getDataTracker().set(PlayerEntityStuff.IS_UNDER_FALL_PROTECTION, true);
                }
            }
        }
        thisOBJ.getDataTracker().set(PlayerEntityStuff.IS_REALLY_SLEEPING, false);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;wakeUp(ZZ)V", shift = At.Shift.AFTER), method = "wakeUp")
    private void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo info) {
        ServerPlayerEntity thisOBJ = (ServerPlayerEntity) (Object) this;
        thisOBJ.getDataTracker().set(PlayerEntityStuff.IS_REALLY_SLEEPING, false);
    }

    @Inject(at = @At(value = "RETURN", ordinal = 4), method = "trySleep", cancellable = true)
    private void alwaysAbleToSleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            if (!this.isCreative()) {
                Vec3d vec3d = Vec3d.ofBottomCenter(pos);
                List<HostileEntity> list = this.getWorld()
                        .getEntitiesByClass(
                                HostileEntity.class,
                                new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0),
                                entity -> entity.isAngryAt(this)
                        );
                if (!list.isEmpty()) {
                    info.setReturnValue(Either.left(SleepFailureReason.NOT_SAFE));
                    return;
                }
            }
            Either<SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> this.incrementStat(Stats.SLEEP_IN_BED));
            ((ServerWorld) this.getWorld()).updateSleepingPlayers();
            info.setReturnValue(either);
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "copyFrom")
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        if (!this.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !oldPlayer.isSpectator() && RuntimeController.getCurrentTParams().keepExperience()) {
            this.experienceLevel = oldPlayer.experienceLevel;
            this.totalExperience = oldPlayer.totalExperience;
            this.experienceProgress = oldPlayer.experienceProgress;
            this.setScore(oldPlayer.getScore());
        }
    }
}
