package online.andrew2007.mythic.mixin;

import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityDataObject.class)
public class EditPlayerDataMixin {
    @Shadow @Final private Entity entity;
    @Inject(at = @At(value = "HEAD"), method = "setNbt", cancellable = true)
    public void setNbt(NbtCompound nbt, CallbackInfo info) {
        Entity entity = this.entity;
        if (entity instanceof PlayerEntity && RuntimeController.getCurrentTParams().editablePlayerData()) {
            UUID uUID = entity.getUuid();
            entity.readNbt(nbt);
            entity.setUuid(uUID);
            info.cancel();
        }
    }
}