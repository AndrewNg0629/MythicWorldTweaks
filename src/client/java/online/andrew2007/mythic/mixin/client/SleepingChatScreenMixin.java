package online.andrew2007.mythic.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.client.SleepingScreenTracker;
import online.andrew2007.mythic.network.payloads.SleepingExtrasPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin extends ChatScreen {
    public SleepingChatScreenMixin(String originalChatText) {
        super(originalChatText);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;<init>(Ljava/lang/String;)V", shift = At.Shift.AFTER), method = "<init>")
    private void SleepingChatScreen(CallbackInfo info) {
        SleepingScreenTracker.onCreate((SleepingChatScreen) (Object) this);
    }

    @Inject(at = @At(value = "HEAD"), method = "close")
    private void close(CallbackInfo info) {
        SleepingScreenTracker.onClose((SleepingChatScreen) (Object) this);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;init()V", shift = At.Shift.AFTER), method = "init")
    private void init(CallbackInfo info) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            SleepingChatScreen thisOBJ = (SleepingChatScreen) (Object) this;
            SleepingScreenTracker.setButtonA(thisOBJ, ButtonWidget.builder(Text.translatable("mythicworldtweaks.sleeping_extras.sleep_button"), button -> ClientPlayNetworking.send(new SleepingExtrasPayload(Unit.INSTANCE)))
                    .dimensions(this.width / 2 - 100, this.height - 60, 200, 20)
                    .build());
            this.addDrawableChild(SleepingScreenTracker.getButtonA(thisOBJ));
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (RuntimeController.getCurrentTParams().sleepingExtras()) {
            ButtonWidget button = SleepingScreenTracker.getButtonA((SleepingChatScreen) (Object) this);
            if (button != null) {
                button.render(context, mouseX, mouseY, delta);
            }
        }
    }
}
