package top.aenp.mwt.mixin;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightMixin {
    @Shadow
    private boolean previouslyKilled;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;generateNewEndGateway()V", shift = At.Shift.AFTER), method = "dragonKilled")
    private void dragonKilled(EnderDragonEntity dragon, CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().alwaysDropDragonEgg()) {
            this.previouslyKilled = false;
        }
    }
}
