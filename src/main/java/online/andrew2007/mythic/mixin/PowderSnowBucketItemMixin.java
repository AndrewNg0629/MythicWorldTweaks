package online.andrew2007.mythic.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PowderSnowBucketItem.class)
public class PowderSnowBucketItemMixin {
    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    private void handleStackChange(PlayerEntity instance, Hand hand, ItemStack itemStack, @Local(argsOnly = true) ItemUsageContext context) {
        if (RuntimeController.getCurrentTParams().itemEditorEnabled()) {
            instance.setStackInHand(hand, ItemUsage.exchangeStack(context.getStack(), instance, new ItemStack(Items.BUCKET)));
        } else {
            instance.setStackInHand(hand, itemStack);
        }
    }
}
