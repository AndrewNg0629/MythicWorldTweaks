package online.andrew2007.mythic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.item.ItemInitializer;
import online.andrew2007.mythic.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MythicWorldTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MythicWorldTweaks");
    public static final String MOD_ID = "mythicworldtweaks";

    @Override
    public void onInitialize() {
        ItemInitializer.generalInitialization();
        PlayerEntityUtil.staticInit();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (RuntimeController.getCurrentTParams().autoDiscardingFireBallEnabled()) {
                FireBallEntityManager.tick();
            }
            WardenEntityUtil.WardenEntityTrack.tick();
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> WardenEntityUtil.WardenEntityTrack.clearEntities());
        ServerLifecycleEvents.SERVER_STARTING.register(ConfigLoader::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ConfigLoader.onServerStopping());
        LOGGER.info("MythicWorld Tweaks mod has been initialized.");
    }
}