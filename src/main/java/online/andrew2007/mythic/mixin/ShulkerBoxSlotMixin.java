package online.andrew2007.mythic.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {
    @Unique
    private static boolean isShulkerBox(ItemStack stack) {
        boolean isShulkerBox = false;
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                isShulkerBox = true;
            }
        }
        return isShulkerBox;
    }

    @Unique
    private static boolean determineNesting(int currentLayer, int maxLayers, ItemStack shulkerBoxStack) {
        Iterable<ItemStack> boxedItemStacks = shulkerBoxStack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).iterateNonEmpty();
        for (ItemStack boxedStack : boxedItemStacks) {
            if (isShulkerBox(boxedStack)) {
                if (currentLayer >= maxLayers) {
                    return false;
                } else {
                    return determineNesting(currentLayer + 1, maxLayers, boxedStack);
                }
            }
        }
        return true;
    }

    @Inject(at = @At("HEAD"), method = "canInsert", cancellable = true)
    private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (isShulkerBox(stack) && RuntimeController.getCurrentTParams().nestableShulkerBoxes()) {
            if (RuntimeController.getCurrentTParams().shulkerBoxNestingLimitEnabled()) {
                info.setReturnValue(determineNesting(1, RuntimeController.getCurrentTParams().shulkerBoxMaxLayers(), stack));
            } else {
                info.setReturnValue(true);
            }
        }
    }
}