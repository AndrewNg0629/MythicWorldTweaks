package online.andrew2007.mythic.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Shadow
    @Final
    private Enchantment.Definition definition;

    @Inject(at = @At(value = "HEAD"), method = "isAcceptableItem", cancellable = true)
    private void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (RuntimeController.getCurrentTParams().bowEnchantmentsForCrossbow() && ItemTags.BOW_ENCHANTABLE.equals(this.definition.supportedItems().getTagKey().orElse(null)) && stack != null) {
            if (stack.getItem().equals(Items.CROSSBOW)) {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "isSupportedItem", cancellable = true)
    private void isSupportedItem(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (RuntimeController.getCurrentTParams().bowEnchantmentsForCrossbow() && ItemTags.BOW_ENCHANTABLE.equals(this.definition.supportedItems().getTagKey().orElse(null)) && stack != null) {
            if (stack.getItem().equals(Items.CROSSBOW)) {
                info.setReturnValue(true);
            }
        }
    }
}
