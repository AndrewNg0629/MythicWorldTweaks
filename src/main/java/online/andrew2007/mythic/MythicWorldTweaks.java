package online.andrew2007.mythic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.item.ItemInitializer;
import online.andrew2007.mythic.modFunctions.EnvironmentDetection;
import online.andrew2007.mythic.modFunctions.FireBallEntityManager;
import online.andrew2007.mythic.modFunctions.WardenEntityStuff;
import online.andrew2007.mythic.network.MythicNetwork;
import online.andrew2007.mythic.network.PlayConfigPushValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MythicWorldTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MythicWorldTweaks");
    public static final String MOD_ID = "mythicworldtweaks";
    public static final String DATA_MOD_ID = "mythicworlddata";
    public static final String MOD_VERSION = Objects.requireNonNull(FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null)).getMetadata().getVersion().getFriendlyString();
    public static final String GAME_VERSION = MinecraftVersion.CURRENT.getName();

    public static void staticInit() {
        try {
            Class.forName("online.andrew2007.mythic.modFunctions.PlayerEntityStuff");
            Class.forName("online.andrew2007.mythic.modFunctions.ItemEntityStuff");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Failed to find specific class to load.", e);
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("MythicWorldTweaks mod starts to be initialized!");
        RuntimeController.loadLocalParamsFromConfig();
        ItemInitializer.generalInitialization();
        staticInit();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (RuntimeController.getCurrentTParams().autoDiscardingFireBallEnabled()) {
                FireBallEntityManager.tick();
            }
            WardenEntityStuff.WardenEntityTrack.tick();
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> WardenEntityStuff.WardenEntityTrack.clearEntities());
        ServerLifecycleEvents.SERVER_STARTING.register(ConfigLoader::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ConfigLoader.onServerStopping();
            if (!EnvironmentDetection.isPhyClient) {
                PlayConfigPushValidator.shutDownExecutor();
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("suicide")
                        .requires(source -> source.isExecutedByPlayer() && RuntimeController.getCurrentTParams().suicideCommand())
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            Entity entity = source.getPlayerOrThrow();
                            entity.kill();
                            source.sendFeedback(() -> Text.translatable("commands.kill.success.single", entity.getDisplayName()), false);
                            return 1;
                        })));
        MythicNetwork.commonInitialization();
    }
}
