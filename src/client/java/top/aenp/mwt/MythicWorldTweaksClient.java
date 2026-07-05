package top.aenp.mwt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import top.aenp.mwt.config.ConfigFileListener;
import top.aenp.mwt.config.ConfigLoader;
import top.aenp.mwt.misc.client.ClientToaster;
import top.aenp.mwt.network.client.MythicClientNetwork;

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