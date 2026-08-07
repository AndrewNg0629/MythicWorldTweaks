package top.aenp.mwt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.item.ItemInitializer;
import top.aenp.mwt.misc.FireBallEntityManager;
import top.aenp.mwt.misc.WardenEntityStuff;
import top.aenp.mwt.network.v2.MythicNetwork;

public class MythicWorldTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MythicWorldTweaks");
    public static final String MOD_ID = "mythicworldtweaks";

    @Override
    public void onInitialize() {
        ConfigManager.initialize();
        MythicNetwork.INSTANCE.initialize();
        ItemInitializer.generalInitialization();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ConfigManager.getConfig().tweaks().valueTweaks().fireballAutoDiscarding().enabled()) {
                FireBallEntityManager.tick();
            }
            WardenEntityStuff.WardenEntityTrack.tick();
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> WardenEntityStuff.WardenEntityTrack.clearEntities());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("suicide")
                        .requires(source -> source.isExecutedByPlayer() && ConfigManager.getConfig().tweaks().syncedToggleTweaks1().suicideCommand())
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            Entity entity = source.getPlayerOrThrow();
                            entity.kill();
                            source.sendFeedback(() -> Text.translatable("commands.kill.success.single", entity.getDisplayName()), false);
                            return 1;
                        })));
        try {
            Class.forName("net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage");
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Failed to touch FluidStorage");
        }
        LOGGER.info("MythicWorldTweaks has been initialized!");
    }
}
