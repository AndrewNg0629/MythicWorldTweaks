package online.andrew2007.mythic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.injectedInterfaces.ItemEntityMethodInjections;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityMethodInjections {
    @Unique
    private final int worldMinY = this.getWorld().getBottomY() + 1;
    @Shadow
    private int itemAge;

    @Unique
    private boolean isUnderProtection = false;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean mythicWorldTweaks$isUnderProtection() {
        return this.isUnderProtection;
    }

    @Override
    public void mythicWorldTweaks$setUnderProtection(boolean value) {
        this.isUnderProtection = value;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;merge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;"),
            method = "merge(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
            , index = 2)
    private static int mergeCountFix(int maxCount) {
        if (RuntimeController.getCurrentTParams().itemEditorEnabled()) {
            return Integer.MAX_VALUE;
        } else {
            return maxCount;
        }
    }

    @Shadow
    protected abstract double getGravity();

    @Unique
    private boolean isUnderProtection() {
        return RuntimeController.getCurrentTParams().playerDeathItemProtectionEnabled() && this.isUnderProtection;
    }

    @Inject(at = @At(value = "RETURN"), method = "getGravity", cancellable = true)
    private void getGravity(CallbackInfoReturnable<Double> info) {
        if (this.isUnderProtection() && this.getY() <= this.worldMinY) {
            info.setReturnValue(0D);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER), method = "tick")
    private void tick(CallbackInfo info) {
        if (this.isUnderProtection()) {
            BlockPos blockPosAbove = this.getBlockPos().up();
            if (this.getY() <= this.worldMinY + 0.3D && !this.getWorld().getBlockState(blockPosAbove).isSolidBlock(this.getWorld(), blockPosAbove)) {
                this.setVelocity(new Vec3d(this.getVelocity().x, 0D, this.getVelocity().z));
                double relevantHeight = Math.abs(this.getY() - this.worldMinY);
                if (relevantHeight <= 0.5D) {
                    if (relevantHeight > 0.1D) {
                        this.setPosition(this.getX(), this.worldMinY, this.getZ());
                    } else {
                        this.setVelocity(Vec3d.ZERO);
                    }
                } else {
                    this.setPosition(this.getX(), this.getY() + 0.5D, this.getZ());
                }
            }
            if (RuntimeController.getCurrentTParams().itemDiscardTicks() <= 0) {
                this.itemAge = 0;
            }
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "isFireImmune", cancellable = true)
    private void isFireImmune(CallbackInfoReturnable<Boolean> info) {
        if (this.isUnderProtection()) {
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "damage", cancellable = true)
    private void damage(CallbackInfoReturnable<Boolean> info) {
        if (this.isUnderProtection()) {
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", cancellable = true)
    private void tryMerge(ItemEntity other, CallbackInfo info) {
        if (RuntimeController.getCurrentTParams().playerDeathItemProtectionEnabled() &&
                (this.mythicWorldTweaks$isUnderProtection()) ^ (other.mythicWorldTweaks$isUnderProtection())
        ) {
            info.cancel();
        }
    }

    @ModifyConstant(constant = @Constant(intValue = 6000), method = "tick")
    private int discardTicks(int value) {
        int itemDiscardTicks = RuntimeController.getCurrentTParams().itemDiscardTicks();
        return itemDiscardTicks > 0 && this.isUnderProtection() ? itemDiscardTicks : value;
    }

    @Inject(at = @At(value = "RETURN"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("isUnderProtection", this.isUnderProtection);
    }

    @Inject(at = @At(value = "RETURN"), method = "readCustomDataFromNbt")
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("isUnderProtection")) {
            this.isUnderProtection = nbt.getBoolean("isUnderProtection");
        } else {
            this.isUnderProtection = false;
        }
    }
}