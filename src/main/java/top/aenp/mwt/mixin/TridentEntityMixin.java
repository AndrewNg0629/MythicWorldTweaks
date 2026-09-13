package top.aenp.mwt.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    @Shadow
    @Final
    private static TrackedData<Byte> LOYALTY;

    @Shadow
    private boolean dealtDamage;

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        if (getY() <= getWorld().getBottomY() - 63 && getDataTracker().get(LOYALTY) > 0 && ConfigManager.getConfig().tweaks().localToggleTweaks1().tridentsReturnFromVoid()) {
            setVelocity(Vec3d.ZERO);
            dealtDamage = true;
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getEntityCollision", cancellable = true)
    public void getEntityCollision(Vec3d currentPosition, Vec3d nextPosition, CallbackInfoReturnable<EntityHitResult> info) {
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().tridentsDamageMultipleTimes()) {
            info.setReturnValue(isNoClip() ? null : super.getEntityCollision(currentPosition, nextPosition));
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;age()V"), method = "age", cancellable = true)
    private void age(CallbackInfo info) {
        if ((pickupType.equals(PickupPermission.ALLOWED) || pickupType.equals(PickupPermission.CREATIVE_ONLY)) && ConfigManager.getConfig().tweaks().localToggleTweaks1().thrownTridentsPersist()) {
            info.cancel();
        }
    }
}