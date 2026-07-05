package top.aenp.mwt.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestPayload;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.config.ConfigLoader;
import top.aenp.mwt.config.RuntimeController;
import top.aenp.mwt.config.v2.NetworkSyncedConfig;
import top.aenp.mwt.network.payloads.LoginConfigPushC2SPayload;
import top.aenp.mwt.network.payloads.LoginConfigPushS2CPayload;
import top.aenp.mwt.network.payloads.ValidationC2SPayload;
import top.aenp.mwt.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.runtimeParams.TransmittableRuntimeParams;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.MythicLoginS2CPayload;
import top.aenp.mwt.network.v2.test.TestLoginC2SPayload;
import top.aenp.mwt.network.v2.test.TestLoginS2CPayload;

@Mixin(value = ClientLoginNetworkHandler.class, priority = 990)
public class ClientLoginNetworkHandlerMixin implements ClientLoginNetworkHandlerMethodInjections {
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
                TransmittableRuntimeParams params
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

    //TODO: Remove
    @Inject(method = "onQueryRequest", at = @At(value = "HEAD"), cancellable = true)
    private void handleRequest(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
        if (packet.queryId() == MythicNetwork.QUERY_ID) {
            MythicLoginS2CPayload payload = (MythicLoginS2CPayload) packet.payload();
            payload.handle(this);
            info.cancel();
        }
    }

    @Override
    public void labmod$onTestLoginS2C(TestLoginS2CPayload payload) {
        MythicWorldTweaks.LOGGER.info("Client received: {}", payload.hello());
        this.connection.send(new LoginQueryResponseC2SPacket(MythicNetwork.QUERY_ID, new TestLoginC2SPayload("Hello server!")));
        this.connection.send(new LoginQueryResponseC2SPacket(MythicNetwork.QUERY_ID, new TestLoginC2SPayload("Hello server!")));
    }

    @Override
    public void labmod$onConfigPush(NetworkSyncedConfig config) {

    }
}
