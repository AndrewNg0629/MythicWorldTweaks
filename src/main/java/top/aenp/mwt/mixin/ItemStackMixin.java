package top.aenp.mwt.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"),
            method = "method_57371(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            index = 1)
    private static int itemCountRangeExpansion(int maxCount) {
        return Integer.MAX_VALUE;
    }

    @Shadow
    public abstract Item getItem();

    @Inject(at = @At(value = "HEAD"), method = "getMaxCount", cancellable = true)
    private void getMaxCount(CallbackInfoReturnable<Integer> info) {
        if (this.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof ShulkerBoxBlock && this.get(DataComponentTypes.CONTAINER) != ContainerComponent.DEFAULT) {
                info.setReturnValue(Math.min(ConfigManager.getConfig().tweaks().valueTweaks().stuffedShulkerBoxStacking().maxStackSize(), Items.SHULKER_BOX.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1)));
            }
        }
    }
}