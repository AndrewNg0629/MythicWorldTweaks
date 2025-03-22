package online.andrew2007.mythic.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.network.payloads.LoginConfigPushC2SPayload;
import online.andrew2007.mythic.network.payloads.LoginConfigPushS2CPayload;
import online.andrew2007.mythic.network.payloads.ValidationC2SPayload;
import online.andrew2007.mythic.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientLoginNetworkHandler.class, priority = 100)
public class ClientLoginNetworkHandlerMixin {
    @Shadow
    @Final
    private ClientConnection connection;
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onQueryRequest", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", remap = false, shift = At.Shift.AFTER), cancellable = true)
    private void onQueryRequest(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
        LoginQueryRequestPayload rawPayload = packet.payload();
        int queryId = packet.queryId();
        if (rawPayload instanceof ValidationS2CPayload(String serverName, String gameVersion, String modVersion)) {
            this.client.execute(() -> {
                if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
                    MythicWorldTweaks.LOGGER.info("The server you are joining has MythicWorldTweaks mod installed.");
                    MythicWorldTweaks.LOGGER.info("Server info: Name: {}, Server Minecraft version: {}, Server mod: MythicWorldTweaks {}", serverName, gameVersion, modVersion);
                    if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
                        if (!MythicWorldTweaks.MOD_VERSION.equals(modVersion)) {
                            MythicWorldTweaks.LOGGER.warn("Current mod version \"{}\" isn't equivalent to the server's, you may be kicked.", MythicWorldTweaks.MOD_VERSION);
                        }
                        this.connection.send(new LoginQueryResponseC2SPacket(queryId, new ValidationC2SPayload(MythicWorldTweaks.MOD_VERSION, ConfigLoader.getAllModIds())));
                    } else {
                        MythicWorldTweaks.LOGGER.warn("Your \"server_play_support\" is disabled, preventing you from responding to validation, you may be kicked by the server");
                    }
                }
            });
            info.cancel();
        } else if (rawPayload instanceof LoginConfigPushS2CPayload(
                online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams params
        )) {
            this.client.execute(() -> {
                if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
                    RuntimeController.receiveConfigPush(params);
                    this.connection.send(new LoginQueryResponseC2SPacket(queryId, new LoginConfigPushC2SPayload(RuntimeController.getCurrentTParams())));
                } else {
                    MythicWorldTweaks.LOGGER.warn("Your \"server_play_support\" is disabled, preventing you from responding to config push, you may be kicked by the server");
                }
            });
            info.cancel();
        }
    }
}
