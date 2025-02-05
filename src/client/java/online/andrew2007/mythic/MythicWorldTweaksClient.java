package online.andrew2007.mythic;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import online.andrew2007.mythic.network.payloads.SleepingExtrasPayload;
import online.andrew2007.mythic.util.client.ClientToaster;
import online.andrew2007.mythic.config.ConfigFileListener;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.util.client.SleepingScreenTracker;

public class MythicWorldTweaksClient implements ClientModInitializer {
	@SuppressWarnings("resource") //Would you like to close the client?
    @Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			ClientToaster.initToaster(client);
			ConfigLoader.tryConfigSystemInit();
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigFileListener.stopListener());
		ClientPlayNetworking.registerGlobalReceiver(SleepingExtrasPayload.ID, (payload, context) -> context.client().execute(
				() -> {
					Screen screen = context.client().currentScreen;
					if (screen instanceof SleepingChatScreen sleepingChatScreen) {
						ButtonWidget button = SleepingScreenTracker.getButtonA(sleepingChatScreen);
						if (button != null) {
							button.visible = false;
						}
					}
				}
		));
	}
}