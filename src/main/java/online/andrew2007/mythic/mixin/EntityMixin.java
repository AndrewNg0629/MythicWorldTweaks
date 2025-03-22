package online.andrew2007.mythic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.ItemEntityStuff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract void setPosition(Vec3d pos);

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract World getWorld();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER), method = "addPassenger")
    private void addPassenger(Entity passenger, CallbackInfo info) {
        Entity thisOBJ = (Entity) (Object) this;
        if (thisOBJ instanceof ServerPlayerEntity playerVehicle) {
            playerVehicle.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(thisOBJ));
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER), method = "removePassenger")
    private void removePassenger(Entity passenger, CallbackInfo info) {
        Entity thisOBJ = (Entity) (Object) this;
        if (thisOBJ instanceof ServerPlayerEntity playerVehicle) {
            playerVehicle.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(thisOBJ));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "interact")
    private void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (RuntimeController.getCurrentTParams().playerRidingGestures()) {
            Entity thisOBJ = (Entity) (Object) this;
            if (thisOBJ instanceof ServerPlayerEntity thisPlayer && !player.getWorld().isClient()) {
                if (thisPlayer.getFirstPassenger() == null && thisPlayer.isSneaking()) {
                    player.startRiding(thisPlayer);
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "tickInVoid", cancellable = true)
    private void tickInVoid(CallbackInfo info) {
        Entity thisOBJ = (Entity) (Object) this;
        if (RuntimeController.getCurrentTParams().playerDeathItemProtection() && thisOBJ instanceof ItemEntity item) {
            if (item.getDataTracker().get(ItemEntityStuff.IS_UNDER_PROTECTION)) {
                this.setPosition(new Vec3d(this.getX(), this.getWorld().getBottomY() - 63, this.getZ()));
                info.cancel();
            }
        }
    }
}