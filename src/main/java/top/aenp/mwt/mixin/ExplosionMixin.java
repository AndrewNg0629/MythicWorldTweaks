package top.aenp.mwt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.Set;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Nullable
    public abstract Entity getEntity();

    @Inject(at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;addAll(Ljava/util/Collection;)Z", remap = false), method = "collectBlocksAndDamageEntities")
    private void collectBlocksAndDamageEntities(CallbackInfo info, @Local Set<BlockPos> blocksToDestroy) {
        boolean shouldDestroyBlocks = true;
        Entity sourceEntity = this.getEntity();
        if (sourceEntity != null) {
            if (sourceEntity.getType().equals(EntityType.CREEPER) && ConfigManager.getConfig().tweaks().localToggleTweaks1().creepersDontBreakBlocks()) {
                shouldDestroyBlocks = false;
            }
        }
        if (!shouldDestroyBlocks) {
            blocksToDestroy.clear();
        }
    }

    @WrapOperation(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;shouldDamage(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/entity/Entity;)Z"))
    private boolean shouldDamage(ExplosionBehavior instance, Explosion explosion, Entity entity, Operation<Boolean> original) {
        boolean tweakedShouldDamage = true;
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().itemExplosionResistance()) {
            EntityType<?> entityType = entity.getType();
            if (entityType.equals(EntityType.ITEM) || entityType.equals(EntityType.ITEM_DISPLAY)) {
                tweakedShouldDamage = false;
            }
        }
        return original.call(instance, explosion, entity) && tweakedShouldDamage;
    }
}