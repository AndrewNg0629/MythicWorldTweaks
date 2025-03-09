package online.andrew2007.mythic;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import online.andrew2007.mythic.config.ConfigFileListener;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.modFunctions.client.ClientToaster;
import online.andrew2007.mythic.network.client.MythicClientNetwork;

public class MythicWorldTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ClientToaster.initToaster(client);
            ConfigLoader.tryConfigSystemInit();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigFileListener.stopListener());
        MythicClientNetwork.clientInitialization();
    }
}