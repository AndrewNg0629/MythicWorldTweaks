package online.andrew2007.mixin;

import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import online.andrew2007.util.MythicWorldTweaksToggle;
import online.andrew2007.util.ReflectionCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityDataObject.class)
public class EditPlayerDataMixin {
    @Inject(at = @At(value = "HEAD"), method = "setNbt", cancellable = true)
    public void setNbt(NbtCompound nbt, CallbackInfo info) {
        EntityDataObject thisOBJ = (EntityDataObject) (Object) this;
        Entity entity = ReflectionCenter.getFieldValue(ReflectionCenter.entity, thisOBJ);
        if (entity instanceof PlayerEntity && MythicWorldTweaksToggle.CAN_EDIT_PLAYER_DATA.isEnabled()) {
            UUID uUID = entity.getUuid();
            entity.readNbt(nbt);
            entity.setUuid(uUID);
            info.cancel();
        }
    }
}