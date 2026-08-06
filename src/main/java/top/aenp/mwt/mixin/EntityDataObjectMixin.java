package top.aenp.mwt.mixin;

import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.UUID;

@Mixin(EntityDataObject.class)
public class EntityDataObjectMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(at = @At(value = "HEAD"), method = "setNbt", cancellable = true)
    public void setNbt(NbtCompound nbt, CallbackInfo info) {
        Entity entity = this.entity;
        if (entity instanceof PlayerEntity) {
            if (ConfigManager.getConfig().tweaks().localToggleTweaks1().editablePlayerData()) {
                UUID uUID = entity.getUuid();
                entity.readNbt(nbt);
                entity.setUuid(uUID);
                info.cancel();
            }
        }
    }
}