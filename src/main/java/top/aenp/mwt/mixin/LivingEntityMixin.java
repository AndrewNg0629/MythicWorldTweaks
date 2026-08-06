package top.aenp.mwt.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(at = @At(value = "HEAD"), method = "tickInVoid", cancellable = true)
    private void tickInVoid(CallbackInfo info) {
        if ((Entity) (Object) this instanceof PlayerEntity player && ConfigManager.getConfig().tweaks().localToggleTweaks1().creativePlayerVoidResistance()) {
            if (player.isInCreativeMode()) {
                info.cancel();
            }
        }
    }
}
