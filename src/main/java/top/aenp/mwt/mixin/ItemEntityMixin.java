package top.aenp.mwt.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.injected.interfaces.ItemEntityMethodInjections;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityMethodInjections {
    @Unique
    private final int worldMinY = getWorld().getBottomY() + 1;
    @Shadow
    private int itemAge;

    @Unique
    private boolean isUnderProtection = false;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;merge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;"),
            method = "merge(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
            , index = 2)
    private static int mergeCountFix(int maxCount) {
        if (ConfigManager.getConfig().itemEditorConfig().enabled()) {
            return Integer.MAX_VALUE;
        } else {
            return maxCount;
        }
    }

    @Override
    public boolean mythicWorldTweaks$isUnderProtection() {
        return isUnderProtection;
    }

    @Override
    public void mythicWorldTweaks$setUnderProtection(boolean value) {
        isUnderProtection = value;
    }

    @Shadow
    protected abstract double getGravity();

    @Unique
    private boolean isUnderProtection() {
        return ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().enabled() && isUnderProtection;
    }

    @Inject(at = @At(value = "RETURN"), method = "getGravity", cancellable = true)
    private void getGravity(CallbackInfoReturnable<Double> info) {
        if (isUnderProtection() && getY() <= worldMinY) {
            info.setReturnValue(0D);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER), method = "tick")
    private void tick(CallbackInfo info) {
        if (isUnderProtection()) {
            BlockPos blockPosAbove = getBlockPos().up();
            if (getY() <= worldMinY + 0.3D && !getWorld().getBlockState(blockPosAbove).isSolidBlock(getWorld(), blockPosAbove)) {
                setVelocity(new Vec3d(getVelocity().x, 0D, getVelocity().z));
                double relevantHeight = Math.abs(getY() - worldMinY);
                if (relevantHeight <= 0.5D) {
                    if (relevantHeight > 0.1D) {
                        setPosition(getX(), worldMinY, getZ());
                    } else {
                        setVelocity(Vec3d.ZERO);
                    }
                } else {
                    setPosition(getX(), getY() + 0.5D, getZ());
                }
            }
            if (ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().itemDespawnTicks() <= 0) {
                itemAge = 0;
            }
        }
    }

    @Inject(at = @At(value = "RETURN"), method = "isFireImmune", cancellable = true)
    private void isFireImmune(CallbackInfoReturnable<Boolean> info) {
        if (isUnderProtection()) {
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "damage", cancellable = true)
    private void damage(CallbackInfoReturnable<Boolean> info) {
        if (isUnderProtection()) {
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", cancellable = true)
    private void tryMerge(ItemEntity other, CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().enabled() &&
                (mythicWorldTweaks$isUnderProtection()) ^ (other.mythicWorldTweaks$isUnderProtection())
        ) {
            info.cancel();
        }
    }

    @ModifyConstant(constant = @Constant(intValue = 6000), method = "tick")
    private int discardTicks(int value) {
        int itemDiscardTicks = ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().itemDespawnTicks();
        return itemDiscardTicks > 0 && isUnderProtection() ? itemDiscardTicks : value;
    }

    @Inject(at = @At(value = "RETURN"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("isUnderProtection", isUnderProtection);
    }

    @Inject(at = @At(value = "RETURN"), method = "readCustomDataFromNbt")
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("isUnderProtection")) {
            isUnderProtection = nbt.getBoolean("isUnderProtection");
        } else {
            isUnderProtection = false;
        }
    }
}