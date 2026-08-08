package top.aenp.mwt.mixin;

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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Unique
    private byte pressTime;
    @Unique
    private byte singleClickInterval;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().playerRiding()) {
            if (isSneaking()) {
                if (pressTime >= 0 && pressTime < 10) {
                    pressTime++;
                } else {
                    pressTime = -1;
                }
            } else {
                if (pressTime > 0 && pressTime <= 10) {
                    if (singleClickInterval > 0) {
                        removeAllPassengers();
                    }
                    singleClickInterval = 0;
                }
                pressTime = 0;
            }
            if (singleClickInterval >= 0) {
                if (singleClickInterval < (byte) 10) {
                    singleClickInterval++;
                } else {
                    singleClickInterval = -1;
                }
            }
        }
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().playerRidingFallProtection()) {
            if (mythicWorldTweaks$isUnderFallProtection()) {
                if ((isOnGround() || isInFluid() || isFallFlying() || (isCreative() && getAbilities().flying))) {
                    mythicWorldTweaks$setUnderFallProtection(false);
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "onDeath")
    private void onDeath(DamageSource damageSource, CallbackInfo info) {
        mythicWorldTweaks$setUnderFallProtection(false);
        mythicWorldTweaks$setReallySleeping(false);
    }

    @Inject(at = @At(value = "HEAD"), method = "onDisconnect")
    private void onDisconnect(CallbackInfo info) {
        Entity passenger = getFirstPassenger();
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().playerRidingFallProtection()) {
            if (passenger != null) {
                if (passenger instanceof ServerPlayerEntity playerPassenger) {
                    playerPassenger.mythicWorldTweaks$setUnderFallProtection(true);
                }
            }
        }
        mythicWorldTweaks$setReallySleeping(false);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;wakeUp(ZZ)V", shift = At.Shift.AFTER), method = "wakeUp")
    private void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo info) {
        mythicWorldTweaks$setReallySleeping(false);
    }

    @Inject(at = @At(value = "RETURN", ordinal = 4), method = "trySleep", cancellable = true)
    private void alwaysAbleToSleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            if (!isCreative()) {
                Vec3d vec3d = Vec3d.ofBottomCenter(pos);
                List<HostileEntity> list = getWorld()
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
            Either<SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> incrementStat(Stats.SLEEP_IN_BED));
            ((ServerWorld) getWorld()).updateSleepingPlayers();
            info.setReturnValue(either);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/TickCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), method = "method_19504", cancellable = true)
    private void triggerAdvancement(Unit unit, CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            info.cancel();
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "copyFrom")
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        if (!getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !oldPlayer.isSpectator() && ConfigManager.getConfig().tweaks().localToggleTweaks1().keepExperienceAfterDeath()) {
            experienceLevel = oldPlayer.experienceLevel;
            totalExperience = oldPlayer.totalExperience;
            experienceProgress = oldPlayer.experienceProgress;
            setScore(oldPlayer.getScore());
        }
    }
}
