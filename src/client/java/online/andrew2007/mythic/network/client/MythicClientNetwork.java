package online.andrew2007.mythic.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.network.payloads.PlayConfigPushPayload;
import online.andrew2007.mythic.network.payloads.SleepingExtrasPayload;

public class MythicClientNetwork {
    @SuppressWarnings("resource") //Would you like to close the client?
    public static void clientInitialization() {
        ClientPlayNetworking.registerGlobalReceiver(SleepingExtrasPayload.ID, (payload, context) -> context.client().execute(() ->
                {
                    context.player().mythicWorldTweaks$setReallySleeping(true);
                    Screen screen = context.client().currentScreen;
                    if (screen instanceof SleepingChatScreen sleepingChatScreen) {
                        ButtonWidget button = sleepingChatScreen.mythicWorldTweaks$getSleepButton();
                        if (button != null) {
                            button.visible = false;
                        }
                    }
                }
        ));
        ClientPlayNetworking.registerGlobalReceiver(PlayConfigPushPayload.ID, (payload, context) -> context.client().execute(() ->
        {
            if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
                RuntimeController.receiveConfigPush(payload.params());
                context.responseSender().sendPacket(new PlayConfigPushPayload(RuntimeController.getCurrentTParams()));
            } else {
                MythicWorldTweaks.LOGGER.warn("Your \"server_play_support\" is disabled, preventing you from responding to config push, you may be kicked by the server");
            }
        }));
    }
}
