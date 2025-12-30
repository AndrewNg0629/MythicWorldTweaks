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
    private void handleStackChange(PlayerEntity instance, Hand hand, ItemStack itemStack, @Local(argsOnly = true) ItemUsageContext context, @Local PlayerEntity player) {
        if (RuntimeController.getCurrentTParams().itemEditorEnabled()) {
            ItemStack usedStack = context.getStack();
            ItemStack bucketStack = new ItemStack(Items.BUCKET);
            if (player.isCreative()) {
                instance.setStackInHand(hand, usedStack);
                if (!player.getInventory().contains(bucketStack)) {
                    player.getInventory().insertStack(bucketStack);
                }
            } else {
                if (usedStack.isEmpty()) {
                    instance.setStackInHand(hand, bucketStack);
                } else {
                    instance.setStackInHand(hand, usedStack);
                    if (!player.getInventory().insertStack(bucketStack)) {
                        player.dropItem(bucketStack, false);
                    }
                }
            }
        } else {
            instance.setStackInHand(hand, itemStack);
        }
    }
}
