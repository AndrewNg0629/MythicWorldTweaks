package top.aenp.mwt.mixin;

import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(Inventory.class)
public interface InventoryMixin {
    @Inject(at = @At(value = "HEAD"), method = "getMaxCountPerStack", cancellable = true)
    private void getMaxCountPerStack(CallbackInfoReturnable<Integer> info) {
        if (ConfigManager.getConfig().itemEditorConfig().enabled()) {
            info.setReturnValue(Integer.MAX_VALUE);
        }
    }
}