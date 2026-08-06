package top.aenp.mwt;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import top.aenp.mwt.misc.client.ClientToaster;

public class MythicWorldTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientToaster.initToaster(MinecraftClient.getInstance());
    }
}