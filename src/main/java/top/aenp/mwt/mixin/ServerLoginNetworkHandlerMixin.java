package top.aenp.mwt.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwl.network.v2.MythicNetwork;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.misc.ReflectionUtils;
import top.aenp.mwt.network.v2.MwtNetwork;
import top.aenp.mwt.network.v2.interfaces.MwtServerLoginNetworkHandler;
import top.aenp.mwt.network.v2.payloads.LoginModIdListC2SPayload;
import top.aenp.mwt.network.v2.payloads.LoginModIdRequestS2CPayload;
import top.aenp.mwt.network.v2.payloads.LoginModVersionC2SPayload;
import top.aenp.mwt.network.v2.payloads.LoginModVersionS2CPayload;

import java.util.Set;

@Mixin(value = ServerLoginNetworkHandler.class, priority = 990)
public abstract class ServerLoginNetworkHandlerMixin implements MwtServerLoginNetworkHandler {
    @Shadow
    @Final
    ClientConnection connection;
    @Shadow
    @Final
    MinecraftServer server;
    @Shadow
    private @Nullable GameProfile profile;
    @Unique
    private MwtNetwork.NegotiationStates negotiationState = MwtNetwork.NegotiationStates.VERSION_S2C;

    @Shadow
    public abstract void disconnect(Text reason);

    @Shadow
    protected abstract void sendSuccessPacket(GameProfile profile);

    @Unique
    private void sendConfig() {
        connection.send(new LoginQueryRequestS2CPacket(MythicNetwork.QUERY_ID, ConfigManager.getInstance().getConfigToSend()));
        if (!server.getPlayerManager().disconnectDuplicateLogins(profile)) {
            ReflectionUtils.setLoginHandlerState((ServerLoginNetworkHandler) (Object) this, 5);
        } else {
            sendSuccessPacket(profile);
        }
    }

    @Inject(method = "tickVerify", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;disconnectDuplicateLogins(Lcom/mojang/authlib/GameProfile;)Z"), cancellable = true)
    private void onTickVerify(GameProfile profile, CallbackInfo info) {
        if (!connection.isLocal() && ConfigManager.getConfig().multiplayerSupportEnabled()) {
            ReflectionUtils.setLoginHandlerState((ServerLoginNetworkHandler) (Object) this, 3);
            connection.send(new LoginQueryRequestS2CPacket(MythicNetwork.QUERY_ID, new LoginModVersionS2CPayload(MwtNetwork.MOD_VERSION, MwtNetwork.PROTOCOL_VERSION)));
            negotiationState = MwtNetwork.NegotiationStates.VERSION_C2S;
            info.cancel();
        }
    }

    @Override
    public void mythicworldtweaks$onModVersion(LoginModVersionC2SPayload version) {
        Validate.validState(negotiationState == MwtNetwork.NegotiationStates.VERSION_C2S, "Unexpected mod version c2s packet.");
        if (version.protocolVersion() == MwtNetwork.PROTOCOL_VERSION) {
            if (ConfigManager.getConfig().modIdValidationConfig().enabled()) {
                negotiationState = MwtNetwork.NegotiationStates.MOD_LIST;
                connection.send(new LoginQueryRequestS2CPacket(MythicNetwork.QUERY_ID, new LoginModIdRequestS2CPayload()));
            } else {
                sendConfig();
            }
        } else {
            disconnect(Text.of("Incompatible client version: " + version.modVersion()));
        }
    }

    @Override
    public void mythicworldtweaks$onModIdList(LoginModIdListC2SPayload list) {
        Validate.validState(negotiationState == MwtNetwork.NegotiationStates.MOD_LIST, "Unexpected mod ID list packet.");
        ImmutableSet<String> receivedMods = ImmutableSet.copyOf(list.modIdList());
        ImmutableSet<String> missingMods = Sets.difference(Set.copyOf(ConfigManager.getConfig().modIdValidationConfig().requiredMods()), receivedMods).immutableCopy();
        ImmutableSet<String> excessMods = Sets.intersection(Set.copyOf(ConfigManager.getConfig().modIdValidationConfig().prohibitedMods()), receivedMods).immutableCopy();
        boolean passed = true;
        StringBuilder failMessage = new StringBuilder("Your installed mods don't meet the requirements to join this server.");
        if (!missingMods.isEmpty()) {
            passed = false;
            failMessage.append("\nInstall those mods: ");
            String missingModsString = missingMods.toString();
            failMessage.append(missingModsString, 1, missingModsString.length() - 1);
        }
        if (!excessMods.isEmpty()) {
            passed = false;
            failMessage.append("\nRemove or disable those mods: ");
            String excessModsString = excessMods.toString();
            failMessage.append(excessModsString, 1, excessModsString.length() - 1);
        }
        if (passed) {
            sendConfig();
        } else {
            disconnect(Text.of(failMessage.toString()));
        }
    }
}
