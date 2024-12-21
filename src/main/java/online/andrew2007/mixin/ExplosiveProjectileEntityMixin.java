package online.andrew2007.mixin;

import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import online.andrew2007.util.FireBallEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(ExplosiveProjectileEntity.class)
public class ExplosiveProjectileEntityMixin {
    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    private void ExplosiveProjectileEntity(CallbackInfo info) {
        if (!((ExplosiveProjectileEntity) (Object) this).getWorld().isClient()) {
            FireBallEntityManager.registerEntity((ExplosiveProjectileEntity) (Object) this);
        }
    }
}