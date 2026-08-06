package top.aenp.mwt.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;cannotPickup()Z"), method = "tickMovement")
    private boolean cannotPickup(ItemEntity instance) {
        return instance.cannotPickup() || (ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().enabled() &&
                instance.mythicWorldTweaks$isUnderProtection() &&
                (ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().preventMobPickup() || ConfigManager.getConfig().tweaks().valueTweaks().playerDeathItemProtection().strictPickup()));
    }
}
