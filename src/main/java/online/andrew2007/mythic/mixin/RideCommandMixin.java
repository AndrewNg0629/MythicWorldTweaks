package online.andrew2007.mythic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.RideCommand;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RideCommand.class)
public class RideCommandMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getType()Lnet/minecraft/entity/EntityType;"), method = "executeMount")
    private static EntityType<?> getType(Entity instance) {
        return instance.getType() == EntityType.PLAYER && RuntimeController.getCurrentTParams().rideablePlayers() ? null : instance.getType();
    }
}