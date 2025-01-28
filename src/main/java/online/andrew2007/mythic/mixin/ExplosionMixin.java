package online.andrew2007.mythic.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;addAll(Ljava/util/Collection;)Z", remap = false), method = "collectBlocksAndDamageEntities")
    private void collectBlocksAndDamageEntities(CallbackInfo info, @Local Set<BlockPos> blocksToDestroy) {
        Explosion thisOBJ = (Explosion) (Object) this;
        boolean shouldDestroyBlocks = true;
        if (thisOBJ.getEntity() != null) {
            if (thisOBJ.getEntity().getType().equals(EntityType.CREEPER) && RuntimeController.getCurrentTParams().blocksFriendlyCreepers()) {
                shouldDestroyBlocks = false;
            }
        }
        if (!shouldDestroyBlocks) {
            blocksToDestroy.clear();
        }
    }

    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "STORE"), ordinal = 0)
    private List<Entity> removeItemEntities(List<Entity> affectedEntities) {
        if (RuntimeController.getCurrentTParams().itemExplosionResistance()) {
            affectedEntities.removeIf(entity -> {
                EntityType<?> entityType = entity.getType();
                return entityType.equals(EntityType.ITEM) || entityType.equals(EntityType.ITEM_DISPLAY);
            });
        }
        return affectedEntities;
    }
}