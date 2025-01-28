package online.andrew2007.mythic;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import online.andrew2007.mythic.config.ConfigLoader;

public class MythicWorldTweaksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> ConfigLoader.tryConfigSystemInit());
	}
}