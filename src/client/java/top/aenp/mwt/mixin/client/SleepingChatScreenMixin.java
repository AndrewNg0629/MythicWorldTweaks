package top.aenp.mwt.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.interfaces.client.MwtSleepingChatScreen;
import top.aenp.mwt.network.v2.payloads.TrySleepC2SPayload;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin extends ChatScreen implements MwtSleepingChatScreen {
    @Unique
    private ButtonWidget sleepButton;

    public SleepingChatScreenMixin(String originalChatText) {
        super(originalChatText);
    }

    @Override
    public ButtonWidget mythicWorldTweaks$getSleepButton() {
        return sleepButton;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;init()V", shift = At.Shift.AFTER), method = "init")
    private void init(CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            ClientPlayerEntity clientPlayerEntity = client.player;
            sleepButton = ButtonWidget.builder(Text.translatable("mythicworldtweaks.sleeping_extras.sleep_button"), button -> clientPlayerEntity.networkHandler.sendPacket(new CustomPayloadC2SPacket(new TrySleepC2SPayload())))
                    .dimensions(width / 2 - 100, height - 60, 200, 20)
                    .build();
            sleepButton.visible = !clientPlayerEntity.mythicWorldTweaks$isReallySleeping();
            addDrawableChild(sleepButton);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            sleepButton.render(context, mouseX, mouseY, delta);
        }
    }
}
