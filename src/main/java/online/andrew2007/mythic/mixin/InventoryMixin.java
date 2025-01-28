package online.andrew2007.mythic.mixin;

import net.minecraft.inventory.Inventory;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public interface InventoryMixin {
    @Inject(at = @At(value = "HEAD"), method = "getMaxCountPerStack", cancellable = true)
    private void getMaxCountPerStack(CallbackInfoReturnable<Integer> info) {
        if (RuntimeController.getCurrentTParams().itemEditorEnabled()) {
            info.setReturnValue(Integer.MAX_VALUE);
        }
    }
}