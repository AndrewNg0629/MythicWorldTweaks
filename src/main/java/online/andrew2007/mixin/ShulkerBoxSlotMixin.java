package online.andrew2007.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import online.andrew2007.util.MythicWorldTweaksToggle;
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

    @Inject(at = @At("HEAD"), method = "canInsert", cancellable = true)
    private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (isShulkerBox(stack) && MythicWorldTweaksToggle.SHULKER_BOX_NESTING.isEnabled()) {
            boolean canBeInserted = true;
            Iterable<ItemStack> boxedItemStacks = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).iterateNonEmpty();
            for (ItemStack boxedStack : boxedItemStacks) {
                if (isShulkerBox(boxedStack) && MythicWorldTweaksToggle.SHULKER_BOX_NESTING_LIMIT.isEnabled()) {
                    canBeInserted = false;
                }
            }
            info.setReturnValue(canBeInserted);
        }
    }
}