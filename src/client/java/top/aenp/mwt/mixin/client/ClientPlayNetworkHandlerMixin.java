package top.aenp.mwt.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.interfaces.MwtClientPlayNetworkHandler;
import top.aenp.mwt.network.v2.payloads.SleepingStateUpdateS2CPayload;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 990)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements MwtClientPlayNetworkHandler {
    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void mythicworldtweaks$onSleepingStateUpdate(SleepingStateUpdateS2CPayload payload) {
        client.player.mythicWorldTweaks$setReallySleeping(payload.isReallySleeping());
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            Screen screen = client.currentScreen;
            if (screen instanceof SleepingChatScreen sleepingChatScreen) {
                ButtonWidget sleepButton = sleepingChatScreen.mythicWorldTweaks$getSleepButton();
                if (sleepButton != null) {
                    sleepButton.visible = !client.player.mythicWorldTweaks$isReallySleeping();
                }
            }
        }
    }
}
