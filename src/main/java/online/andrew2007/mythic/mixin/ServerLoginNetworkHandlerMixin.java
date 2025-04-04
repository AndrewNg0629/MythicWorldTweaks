package online.andrew2007.mythic.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.network.MythicNetwork;
import online.andrew2007.mythic.network.payloads.LoginConfigPushC2SPayload;
import online.andrew2007.mythic.network.payloads.LoginConfigPushS2CPayload;
import online.andrew2007.mythic.network.payloads.ValidationC2SPayload;
import online.andrew2007.mythic.network.payloads.ValidationS2CPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLoginNetworkHandler.class, priority = 999)
public abstract class ServerLoginNetworkHandlerMixin {
    @Shadow
    @Final
    ClientConnection connection;
    @Shadow
    @Final
    MinecraftServer server;
    @Shadow
    private int loginTicks;
    @Unique
    private boolean isValidationRequestSent = false;
    @Unique
    private boolean isValidationProcessed = false;
    @Unique
    private boolean isValidationPassed = false;
    @Unique
    private String failReason = "Player validation response timed out, you must have MythicWorldTweaks " + MythicWorldTweaks.MOD_VERSION + " installed and its \"server_play_support\" config enabled.";
    @Unique
    private int tickSincePush;
    @Unique
    private boolean isConfigPushed = false;
    @Unique
    private boolean isConfigPushResponded = false;
    @Unique
    private boolean isConfigPushSuccess = false;

    @Shadow
    public abstract void disconnect(Text reason);

    @Inject(at = @At(value = "HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo info) {
        if (!this.connection.isLocal() && RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
            if (!isValidationRequestSent) {
                this.connection.send(new LoginQueryRequestS2CPacket(MythicNetwork.registerRequest(ValidationS2CPayload.payloadId),
                        new ValidationS2CPayload(
                                RuntimeController.getLocalRuntimeParams().serverName(),
                                MythicWorldTweaks.GAME_VERSION,
                                MythicWorldTweaks.MOD_VERSION
                        )));
                this.isValidationRequestSent = true;
            }
            if (!this.isValidationProcessed) {
                if (++this.loginTicks >= 60) {
                    this.disconnect(Text.of(this.failReason));
                }
                info.cancel();
                return;
            } else {
                if (!this.isValidationPassed) {
                    this.disconnect(Text.of(this.failReason));
                    info.cancel();
                    return;
                }
            }
            if (!this.isConfigPushed) {
                this.tickSincePush = this.loginTicks;
                this.connection.send(new LoginQueryRequestS2CPacket(MythicNetwork.registerRequest(LoginConfigPushS2CPayload.payloadId),
                        new LoginConfigPushS2CPayload(RuntimeController.getCurrentTParams())));
                this.isConfigPushed = true;
            }
            if (!this.isConfigPushResponded) {
                if (++this.loginTicks >= this.tickSincePush + 60) {
                    this.disconnect(Text.of("Config push response timed out, regarded as failure."));
                }
                info.cancel();
            } else {
                if (!this.isConfigPushSuccess) {
                    this.disconnect(Text.of("Config push failed, received config is not equivalent to the server's."));
                    info.cancel();
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "onQueryResponse", cancellable = true)
    private void onQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo info) {
        LoginQueryResponsePayload rawPayload = packet.response();
        if (rawPayload != null) {
            if (rawPayload instanceof ValidationC2SPayload(String modVersion, java.util.Set<String> allModIds)) {
                this.server.execute(() -> {
                    if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
                        boolean versionValidationFailed = false;
                        boolean modIdsValidationFailed = false;
                        if (!MythicWorldTweaks.MOD_VERSION.equals(modVersion)) {
                            versionValidationFailed = true;
                            this.failReason = String.format("The version of your MythicWorldTweaks is %s, while the version %s is required to join the server.", modVersion, MythicWorldTweaks.MOD_VERSION);
                        }
                        if (RuntimeController.getLocalRuntimeParams().modIdValidationEnabled() && !versionValidationFailed) {
                            ImmutableSet<String> submittedModIds = ImmutableSet.copyOf(allModIds);
                            ImmutableSet<String> requiredModIds = ImmutableSet.copyOf(RuntimeController.getLocalRuntimeParams().modIdList());
                            ImmutableSet<String> modIdsNotPresent = Sets.difference(requiredModIds, submittedModIds).immutableCopy();
                            if (!modIdsNotPresent.isEmpty()) {
                                modIdsValidationFailed = true;
                                StringBuilder missingModsMessage = new StringBuilder();
                                for (String modId : modIdsNotPresent) {
                                    if (!missingModsMessage.isEmpty()) {
                                        missingModsMessage.append(", ");
                                    }
                                    missingModsMessage.append(modId);
                                }
                                this.failReason = String.format("Some mods required by the server are missing on your side, please have them installed: %s", missingModsMessage);
                            }
                        }
                        this.isValidationPassed = !versionValidationFailed && !modIdsValidationFailed;
                        this.isValidationProcessed = true;
                    }
                });
                info.cancel();
            } else if (rawPayload instanceof LoginConfigPushC2SPayload(
                    online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams params
            )) {
                this.server.execute(() -> {
                    if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
                        this.isConfigPushSuccess = params.equals(RuntimeController.getCurrentTParams());
                        this.isConfigPushResponded = true;
                    }
                });
                info.cancel();
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"),
            method = "onQueryResponse", cancellable = true)
    private void handleAbnormalPacket(LoginQueryResponseC2SPacket packet, CallbackInfo info) {
        if (RuntimeController.getLocalRuntimeParams().serverPlaySupportEnabled()) {
            this.server.execute(() -> {
                MythicWorldTweaks.LOGGER.warn("Received unrecognized login query response packet from client.");
                MythicWorldTweaks.LOGGER.warn("Maybe it is caused by missing mods, or simply the packet reflection from clients without MythicWorldTweaks");
            });
            info.cancel();
        }
    }
}
