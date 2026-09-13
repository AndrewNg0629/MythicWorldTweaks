package top.aenp.mwt.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.network.v2.interfaces.MwtServerPlayNetworkHandler;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicPlayC2SPayload;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 990)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler implements MwtServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "onCustomPayload", at = @At(value = "HEAD"), cancellable = true)
    private void handleMythicPayloads(CustomPayloadC2SPacket packet, CallbackInfo info) {
        CustomPayload payload = packet.payload();
        if (payload instanceof MythicPlayC2SPayload mythicPayload) {
            mythicPayload.handle(this);
            info.cancel();
        }
    }

    @Override
    public void mythicworldtweaks$onTrySleep() {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            if (player.isSleeping()) {
                if (player.getWorld().isDay()) {
                    player.sendMessage(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW.getMessage(), true);
                } else {
                    player.mythicWorldTweaks$setReallySleeping(true);
                    Criteria.SLEPT_IN_BED.trigger(player);
                    if (!player.getServerWorld().isSleepingEnabled()) {
                        player.sendMessage(Text.translatable("sleep.not_possible"), true);
                    }
                    player.getServerWorld().updateSleepingPlayers();
                }
            }
        }
    }
}
