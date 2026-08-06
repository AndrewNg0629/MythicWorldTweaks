package top.aenp.mwt.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.RideCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(RideCommand.class)
public class RideCommandMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getType()Lnet/minecraft/entity/EntityType;"), method = "executeMount")
    private static EntityType<?> getType(Entity instance) {
        return instance.getType() == EntityType.PLAYER && ConfigManager.getConfig().tweaks().localToggleTweaks1().playerRiding() ? null : instance.getType();
    }
}