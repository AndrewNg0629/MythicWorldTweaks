package online.andrew2007.mythic.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.util.ReflectionCenter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    @Shadow
    @Final
    private static TrackedData<Byte> LOYALTY;

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        TridentEntity thisOBJ = (TridentEntity) (Object) this;
        if (thisOBJ.getY() <= thisOBJ.getWorld().getBottomY() + 12 && thisOBJ.getDataTracker().get(LOYALTY) > 0 && RuntimeController.getCurrentTParams().voidReturnableTrident()) {
            thisOBJ.setVelocity(Vec3d.ZERO);
            ReflectionCenter.setFieldValue(ReflectionCenter.dealtDamage, thisOBJ, true);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getEntityCollision", cancellable = true)
    public void getEntityCollision(Vec3d currentPosition, Vec3d nextPosition, CallbackInfoReturnable<EntityHitResult> info) {
        if (RuntimeController.getCurrentTParams().multiTridentDamage()) {
            info.setReturnValue(this.isNoClip() ? null : super.getEntityCollision(currentPosition, nextPosition));
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;age()V"), method = "age", cancellable = true)
    private void age(CallbackInfo info) {
        if (this.pickupType.equals(PickupPermission.ALLOWED) && RuntimeController.getCurrentTParams().persistentTridents()) {
            info.cancel();
        }
    }
}