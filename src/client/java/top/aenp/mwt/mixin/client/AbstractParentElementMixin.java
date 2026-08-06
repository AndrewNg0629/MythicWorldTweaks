package top.aenp.mwt.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractParentElement.class)
public class AbstractParentElementMixin {
    @Inject(method = "setFocused", at = @At(value = "HEAD"), cancellable = true)
    private void setFocused(Element focused, CallbackInfo info) {
        if (MinecraftClient.getInstance().currentScreen instanceof SleepingChatScreen) {
            if (focused instanceof ButtonWidget) {
                info.cancel();
            }
        }
    }
}
