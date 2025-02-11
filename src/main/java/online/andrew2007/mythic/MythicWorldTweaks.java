package online.andrew2007.mythic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.item.ItemInitializer;
import online.andrew2007.mythic.network.MythicNetwork;
import online.andrew2007.mythic.util.FireBallEntityManager;
import online.andrew2007.mythic.util.PlayerEntityUtil;
import online.andrew2007.mythic.util.WardenEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MythicWorldTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MythicWorldTweaks");
    public static final String MOD_ID = "mythicworldtweaks";
    public static final String MOD_VERSION = Objects.requireNonNull(FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null)).getMetadata().getVersion().getFriendlyString();
    public static final String GAME_VERSION = MinecraftVersion.CURRENT.getName();

    @Override
    public void onInitialize() {
        LOGGER.info("MythicWorldTweaks mod starts to be initialized!");
        RuntimeController.loadLocalParamsFromConfig();
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
        MythicNetwork.commonInitialization();
    }
}
