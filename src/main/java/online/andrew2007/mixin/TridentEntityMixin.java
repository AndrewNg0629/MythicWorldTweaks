package online.andrew2007.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import online.andrew2007.util.MythicWorldTweaksToggle;
import online.andrew2007.util.ReflectionCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        TridentEntity thisOBJ = (TridentEntity) (Object) this;
        if (thisOBJ.getY() <= thisOBJ.getWorld().getBottomY() + 12 && thisOBJ.getDataTracker().get(ReflectionCenter.getFieldValue(ReflectionCenter.LOYALTY, null)) > 0 && MythicWorldTweaksToggle.TRIDENT_VOID_RETURN.isEnabled()) {
            thisOBJ.setVelocity(Vec3d.ZERO);
            ReflectionCenter.setFieldValue(ReflectionCenter.dealtDamage, thisOBJ, true);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getEntityCollision", cancellable = true)
    public void getEntityCollision(Vec3d currentPosition, Vec3d nextPosition, CallbackInfoReturnable<EntityHitResult> info) {
        if (MythicWorldTweaksToggle.TRIDENT_CAN_DAMAGE_MULTIPLE_TIMES.isEnabled()) {
            info.setReturnValue(this.isNoClip() ? null : super.getEntityCollision(currentPosition, nextPosition));
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;age()V"), method = "age", cancellable = true)
    private void age(CallbackInfo info) {
        if (this.pickupType.equals(PickupPermission.ALLOWED) && MythicWorldTweaksToggle.PLAYER_THROWN_TRIDENT_PERSISTENCE.isEnabled()) {
            info.cancel();
        }
    }
}