package online.andrew2007.mythic.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow @Final private MatrixStack matrices;
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"),
            method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    private int drawText(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow, @Local(argsOnly = true, ordinal = 0) int originalX, @Local(argsOnly = true, ordinal = 1) int originalY, @Local(argsOnly = true) ItemStack itemStack) {
        if (RuntimeController.getCurrentTParams().itemEditorEnabled()) {
            int count = itemStack.getCount();
            if (count >= 100000) {
                if (count < 1000000) {
                    text = processDouble((double) count / 1000) + "k";
                } else if (count < 1000000000) {
                    text = processDouble((double) count / 1000000) + "M";
                } else {
                    text = processDouble((double) count / 1000000000) + "G";
                }
            }
            float scale = switch (text.length()) {
                case 0, 1, 2 -> 1.0F;
                case 3 -> 5.0F / 6.0F;
                case 4 -> 0.625F;
                default -> 0.5F;
            };
            this.matrices.translate((originalX + 16) * (1 - scale), (originalY + 16) * (1 - scale), 0.0F);
            this.matrices.scale(scale, scale, 1.0F);
            x = originalX + 19 - 2 - textRenderer.getWidth(text);
        }
        return instance.drawText(textRenderer, text, x, y, color, shadow);
    }

    @Unique
    private static String processDouble(double num) {
        String string = String.valueOf(num).substring(0, 3);
        if (string.endsWith(".")) {
            string = string.substring(0, 2);
        }
        return string;
    }
}
