package online.andrew2007.mythic.mixin;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonFight.class)
public abstract class EnderDragonFightMixin {
    @Shadow
    private boolean previouslyKilled;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;generateNewEndGateway()V", shift = At.Shift.AFTER), method = "dragonKilled")
    private void dragonKilled(EnderDragonEntity dragon, CallbackInfo info) {
        if (RuntimeController.getCurrentTParams().alwaysDragonEgg()) {
            this.previouslyKilled = false;
        }
    }
}
