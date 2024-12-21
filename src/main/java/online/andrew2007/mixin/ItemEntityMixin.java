package online.andrew2007.mixin;

import net.minecraft.entity.ItemEntity;
import online.andrew2007.util.MythicWorldTweaksToggle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;merge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;"),
            method = "merge(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
            , index = 2)
    private static int mergeCountFix(int maxCount) {
        if (MythicWorldTweaksToggle.ITEM_MODIFICATIONS.isEnabled()) {
            return Integer.MAX_VALUE;
        } else {
            return maxCount;
        }
    }
}