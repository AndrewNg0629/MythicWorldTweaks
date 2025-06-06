package online.andrew2007.mythic.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
    @Shadow public abstract Item getItem();

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"),
            method = "method_57371(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            index = 1)
    private static int itemCountRangeExpansion(int maxCount) {
        return Integer.MAX_VALUE;
    }

    @Inject(at = @At(value = "HEAD"), method = "getMaxCount", cancellable = true)
    private void getMaxCount(CallbackInfoReturnable<Integer> info) {
        if (this.getItem() instanceof BlockItem blockItem && RuntimeController.getCurrentTParams().stuffedShulkerBoxStackLimitEnabled()) {
            if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                if (this.get(DataComponentTypes.CONTAINER) != ContainerComponent.DEFAULT) {
                    info.setReturnValue(Math.min(RuntimeController.getCurrentTParams().shulkerBoxMaxStackSize(), Items.SHULKER_BOX.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1)));
                }
            }
        }
    }
}