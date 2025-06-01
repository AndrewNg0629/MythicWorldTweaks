package online.andrew2007.mythic.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;
import online.andrew2007.mythic.injectedInterfaces.ServerPlayerEntityMethodInjections;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerEntityMethodInjections {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    private byte pressTime;
    @Unique
    private byte singleClickInterval;
    @Unique
    private boolean isDuringValidation = false;
    @Unique
    private short pastValidationTicks = 0;
    @Unique
    private String failReason = null;
    @Unique
    private boolean isValidationProcessed = false;
    @Unique
    private boolean isValidationPassed = false;

    @Override
    public void mythicWorldTweaks$onPlayConfigPush() {
        this.isDuringValidation = true;
        this.pastValidationTicks = 0;
        this.failReason = "Config push response timed out, regarded as failure.";
        this.isValidationProcessed = false;
        this.isValidationPassed = false;
    }

    @Override
    public void mythicWorldTweaks$onPlayConfigPushResponse(TransmittableRuntimeParams params) {
        if (RuntimeController.getCurrentTParams().equals(params)) {
            this.isValidationProcessed = true;
            this.isValidationPassed = true;
        } else {
            this.isValidationProcessed = true;
            this.isValidationPassed = false;
            this.failReason = "Config push failed, received config is not equivalent to the server's.";
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo info) {
        if (RuntimeController.getCurrentTParams().playerRidingGestures()) {
            if (this.isSneaking()) {
                if (this.pressTime >= 0 && this.pressTime < 10) {
                    this.pressTime++;
                } else {
                    this.pressTime = -1;
                }
            } else {
                if (this.pressTime > 0 && this.pressTime <= 10) {
                    if (this.singleClickInterval > 0) {
                        this.removeAllPassengers();
                    }
                    this.singleClickInterval = 0;
                }
                this.pressTime = 0;
            }
            if (this.singleClickInterval >= 0) {
                if (singleClickInterval < (byte) 10) {
                    this.singleClickInterval++;
                } else {
                    this.singleClickInterval = -1;
                }
            }
        }
        if (RuntimeController.getCurrentTParams().playerRidingProtection()) {
            if (this.mythicWorldTweaks$isUnderFallProtection()) {
                if ((this.isOnGround() || this.isInFluid() || this.isFallFlying() || (this.isCreative() && this.getAbilities().flying))) {
                    this.mythicWorldTweaks$setUnderFallProtection(false);
                }
            }
        }
        if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
            if (this.isDuringValidation) {
                if (!this.isValidationProcessed) {
                    if (++this.pastValidationTicks >= 60) {
                        this.isDuringValidation = false;
                        this.networkHandler.disconnect(Text.of(this.failReason));
                        info.cancel();
                    }
                } else {
                    if (!this.isValidationPassed) {
                        this.networkHandler.disconnect(Text.of(this.failReason));
                        info.cancel();
                    } else {
                        this.isDuringValidation = false;
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "onDeath")
    private void onDeath(DamageSource damageSource, CallbackInfo info) {
        this.mythicWorldTweaks$setUnderFallProtection(false);
        this.mythicWorldTweaks$setReallySleeping(false);
    }

    @Inject(at = @At(value = "HEAD"), method = "onDisconnect")
    private void onDisconnect(CallbackInfo info) {
        Entity passenger = this.getFirstPassenger();
        if (RuntimeController.getCurrentTParams().playerRidingProtection()) {
            if (passenger != null) {
                if (passenger instanceof ServerPlayerEntity playerPassenger) {
                    playerPassenger.mythicWorldTweaks$setUnderFallProtection(true);
                }
            }
        }
        this.mythicWorldTweaks$setReallySleeping(false);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;wakeUp(ZZ)V", shift = At.Shift.AFTER), method = "wakeUp")
    private void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo info) {
        this.mythicWorldTweaks$setReallySleeping(false);
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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/TickCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), method = "method_19504", cancellable = true)
    private void triggerAdvancement(Unit unit, CallbackInfo info) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            info.cancel();
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
