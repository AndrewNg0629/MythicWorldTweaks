package online.andrew2007.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import online.andrew2007.util.MythicWorldTweaksToggle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"),
            method = "method_57371(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            index = 1)
    private static int itemCountRangeExpansion(int maxCount) {
        return Integer.MAX_VALUE;
    }

    @Inject(at = @At(value = "HEAD"), method = "getMaxCount", cancellable = true)
    private void getMaxCount(CallbackInfoReturnable<Integer> info) {
        ItemStack thisOBJ = (ItemStack) (Object) this;
        if (thisOBJ.getItem() instanceof BlockItem blockItem && MythicWorldTweaksToggle.LIMIT_STUFFED_SHULKER_BOX_STACKING.isEnabled()) {
            if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                if (thisOBJ.get(DataComponentTypes.CONTAINER) != ContainerComponent.DEFAULT) {
                    info.setReturnValue(1);
                }
            }
        }
    }
}