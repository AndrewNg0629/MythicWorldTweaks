package top.aenp.mwt.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.payloads.BedIdleSignalPayload;

import java.util.Objects;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin extends ChatScreen {
    @Unique
    private ButtonWidget sleepButton;

    public SleepingChatScreenMixin(String originalChatText) {
        super(originalChatText);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;init()V", shift = At.Shift.AFTER), method = "init")
    private void init(CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            this.sleepButton = ButtonWidget.builder(Text.translatable("mythicworldtweaks.sleeping_extras.sleep_button"), button -> {
                        assert Objects.requireNonNull(this.client).player != null;
                        this.client.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(new BedIdleSignalPayload()));
                        button.visible = false;
                    })
                    .dimensions(this.width / 2 - 100, this.height - 60, 200, 20)
                    .build();
            this.addDrawableChild(this.sleepButton);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            this.sleepButton.render(context, mouseX, mouseY, delta);
        }
    }
}
