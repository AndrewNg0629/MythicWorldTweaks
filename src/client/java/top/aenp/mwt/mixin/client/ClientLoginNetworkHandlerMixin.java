package top.aenp.mwt.mixin.client;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.MythicNetwork;
import top.aenp.mwt.network.v2.injections.ClientLoginNetworkHandlerMethodInjections;
import top.aenp.mwt.network.v2.payloads.LoginModIdListC2SPayload;
import top.aenp.mwt.network.v2.payloads.LoginModVersionC2SPayload;
import top.aenp.mwt.network.v2.payloads.LoginModVersionS2CPayload;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginS2CPayload;

@Mixin(value = ClientLoginNetworkHandler.class, priority = 990)
public class ClientLoginNetworkHandlerMixin implements ClientLoginNetworkHandlerMethodInjections {
    @Shadow
    @Final
    private ClientConnection connection;

    @Override
    public void mythicworldtweaks$onModVersion(LoginModVersionS2CPayload version) {
        if (ConfigManager.getConfig().multiplayerSupportEnabled()) {
            MythicWorldTweaks.LOGGER.info("Server initiated negotiation.");
            if (version.protocolVersion() == MythicNetwork.PROTOCOL_VERSION) {
                connection.send(new LoginQueryResponseC2SPacket(MythicNetwork.QUERY_ID, new LoginModVersionC2SPayload(MythicNetwork.MOD_VERSION, MythicNetwork.PROTOCOL_VERSION)));
            } else {
                String message = "Incompatible server version: " + version.modVersion();
                connection.disconnect(Text.of(message));
                MythicWorldTweaks.LOGGER.warn(message);
            }
        } else {
            MythicWorldTweaks.LOGGER.warn("You didn't enable multiplayer support, you may be kicked by the server.");
        }
    }

    @Override
    public void mythicworldtweaks$onModIdRequest() {
        connection.send(new LoginQueryResponseC2SPacket(MythicNetwork.QUERY_ID, new LoginModIdListC2SPayload(MythicNetwork.ALL_MODS.asList())));
    }

    @Inject(method = "onQueryRequest", at = @At(value = "HEAD"), cancellable = true)
    private void handleRequest(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
        if (packet.queryId() == MythicNetwork.QUERY_ID) {
            MythicLoginS2CPayload payload = (MythicLoginS2CPayload) packet.payload();
            payload.handle(this);
            info.cancel();
        }
    }
}
