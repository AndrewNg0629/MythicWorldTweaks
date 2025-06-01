package online.andrew2007.mythic.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.injectedInterfaces.PlayerEntityMethodInjections;
import online.andrew2007.mythic.modFunctions.PlayerEntityStuff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityMethodInjections {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private final boolean isFake = PlayerEntityStuff.determineFake((PlayerEntity) (Object) this);

    @Unique
    private boolean isUnderFallProtection = false;

    @Unique
    private boolean isReallySleeping = false;

    @Override
    public boolean mythicWorldTweaks$isFake() {
        return this.isFake;
    }

    @Override
    public boolean mythicWorldTweaks$isUnderFallProtection() {
        return this.isUnderFallProtection;
    }

    @Override
    public void mythicWorldTweaks$setUnderFallProtection(boolean value) {
        this.isUnderFallProtection = value;
    }

    @Override
    public boolean mythicWorldTweaks$isReallySleeping() {
        return this.isReallySleeping;
    }

    @Override
    public void mythicWorldTweaks$setReallySleeping(boolean value) {
        this.isReallySleeping = value;
    }

    @Inject(at = @At(value = "HEAD"), method = "handleFallDamage", cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.mythicWorldTweaks$isUnderFallProtection()) {
                serverPlayerEntity.mythicWorldTweaks$setUnderFallProtection(false);
                info.setReturnValue(!RuntimeController.getCurrentTParams().playerRidingProtection());
            }
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z", ordinal = 0), method = "tick")
    private boolean sleepingTimerControl(PlayerEntity instance) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            return instance.isSleeping() && instance.mythicWorldTweaks$isReallySleeping();
        } else {
            return instance.isSleeping();
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z", ordinal = 0), method = "canResetTimeBySleeping")
    private boolean canReallySkipNight(PlayerEntity instance) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            return instance.isSleeping() && instance.mythicWorldTweaks$isReallySleeping();
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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setPickupDelay(I)V", shift = At.Shift.AFTER), method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
    private void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info, @Local ItemEntity itemEntity) {
        if (RuntimeController.getCurrentTParams().playerDeathItemProtectionEnabled() && this.isDead()) {
            itemEntity.mythicWorldTweaks$setUnderProtection(true);
            if (!retainOwnership && RuntimeController.getCurrentTParams().strictPickup()) {
                itemEntity.setThrower(this);
            }
        }
    }

    @ModifyConstant(constant = @Constant(floatValue = 0.5F), method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
    private float throwSpeed(float constant) {
        if (RuntimeController.getCurrentTParams().playerDeathItemProtectionEnabled()) {
            return this.getY() < this.getWorld().getBottomY() - 16 ? 0.05F : 0.15F;
        } else {
            return constant;
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("isUnderFallProtection", this.isUnderFallProtection);
    }

    @Inject(at = @At(value = "RETURN"), method = "readCustomDataFromNbt")
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("isUnderFallProtection")) {
            this.isUnderFallProtection = nbt.getBoolean("isUnderFallProtection");
        } else {
            this.isUnderFallProtection = false;
        }
    }
}
