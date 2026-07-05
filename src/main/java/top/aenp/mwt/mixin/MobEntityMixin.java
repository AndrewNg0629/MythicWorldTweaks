package top.aenp.mwt.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import top.aenp.mwt.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;cannotPickup()Z"), method = "tickMovement")
    private boolean cannotPickup(ItemEntity instance) {
        return instance.cannotPickup() || (RuntimeController.getCurrentTParams().playerDeathItemProtectionEnabled() &&
                instance.mythicWorldTweaks$isUnderProtection() &&
                (RuntimeController.getCurrentTParams().mobPickupProtection() || RuntimeController.getCurrentTParams().strictPickup()));
    }
}
