package online.andrew2007.mythic.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.registry.entry.RegistryEntry;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @WrapOperation(method = "wearsGoldArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;matches(Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
    private static <T> boolean wearsGoldArmor(RegistryEntry<T> instance, RegistryEntry<T> registryEntry, Operation<Boolean> original, @Local ItemStack itemStack) {
        boolean result = original.call(instance, registryEntry);
        if (RuntimeController.getCurrentTParams().goldTrimsCalmPiglins()) {
            boolean withGoldTrims = false;
            ArmorTrim trim = itemStack.getComponents().getOrDefault(DataComponentTypes.TRIM, null);
            if (trim != null) {
                withGoldTrims = trim.getMaterial().matchesKey(ArmorTrimMaterials.GOLD);
            }
            return result || withGoldTrims;
        } else {
            return result;
        }
    }

}
