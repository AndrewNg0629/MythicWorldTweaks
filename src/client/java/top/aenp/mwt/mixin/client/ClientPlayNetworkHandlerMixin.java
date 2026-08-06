package top.aenp.mwt.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.injections.ClientPlayNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.SleepingStateUpdateS2CPayload;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicPlayS2CPayload;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 990)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements ClientPlayNetworkHandlerMethodInjections {
    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(method = "onCustomPayload", at = @At(value = "HEAD"), cancellable = true)
    private void handleMythicPayload(CustomPayload payload, CallbackInfo info) {
        if (payload instanceof MythicPlayS2CPayload mythicPayload) {
            mythicPayload.handle(this);
            info.cancel();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void mythicworldtweaks$onSleepingStateUpdate(SleepingStateUpdateS2CPayload payload) {
        this.client.player.mythicWorldTweaks$setReallySleeping(payload.isReallySleeping());
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            Screen screen = this.client.currentScreen;
            if (screen instanceof SleepingChatScreen sleepingChatScreen) {
                ButtonWidget sleepButton = sleepingChatScreen.mythicWorldTweaks$getSleepButton();
                if (sleepButton != null) {
                    sleepButton.visible = !this.client.player.mythicWorldTweaks$isReallySleeping();
                }
            }
        }
    }
}
