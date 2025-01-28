package online.andrew2007.mythic.mixin;

import com.google.common.collect.Interner;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public class ItemSettingsMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Interner;intern(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "getComponents")
    private <E> E noInterning(Interner<ComponentMap> instance, E e) {
        return e;
    }

    @Inject(at = @At(value = "RETURN"), method = "getComponents", cancellable = true)
    private void deInterning(CallbackInfoReturnable<ComponentMap> info) {
        ComponentMap componentMap = info.getReturnValue();
        if (componentMap == DataComponentTypes.DEFAULT_ITEM_COMPONENTS) {
            componentMap = ComponentMap.builder().addAll(DataComponentTypes.DEFAULT_ITEM_COMPONENTS).build();
        }
        info.setReturnValue(componentMap);
    }
}
