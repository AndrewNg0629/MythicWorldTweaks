package online.andrew2007.mythic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.ItemEntityStuff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Unique
    private final int worldMinY = this.getWorld().getBottomY() + 1;
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
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
        return RuntimeController.getCurrentTParams().playerDeathItemProtectionEnabled() && this.getDataTracker().get(ItemEntityStuff.IS_UNDER_PROTECTION);
    }

    @Inject(at = @At(value = "TAIL"), method = "initDataTracker")
    private void initDataTracker(DataTracker.Builder builder, CallbackInfo info) {
        builder.add(ItemEntityStuff.IS_UNDER_PROTECTION, false);
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
                (this.getDataTracker().get(ItemEntityStuff.IS_UNDER_PROTECTION) ^ other.getDataTracker().get(ItemEntityStuff.IS_UNDER_PROTECTION))
        ) {
            info.cancel();
        }
    }

    @ModifyConstant(constant = @Constant(intValue = 6000), method = "tick")
    private int discardTicks(int value) {
        int itemDiscardTicks = RuntimeController.getCurrentTParams().itemDiscardTicks();
        return itemDiscardTicks > 0 ? itemDiscardTicks : value;
    }
}