package online.andrew2007.mythic.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.network.payloads.SleepingExtrasPayload;
import online.andrew2007.mythic.util.PlayerEntityUtil;

public class MWTNetwork {
    public static final PacketCodec<ByteBuf, Unit> UNIT_CODEC = new PacketCodec<>() {
        public Unit decode(ByteBuf byteBuf) {
            return Unit.INSTANCE;
        }
        public void encode(ByteBuf byteBuf, Unit unit) {}
    };
    @SuppressWarnings("resource") //Would you like to close the server?
    public static void commonInitialization() {
        PayloadTypeRegistry.playC2S().register(SleepingExtrasPayload.ID, SleepingExtrasPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SleepingExtrasPayload.ID, SleepingExtrasPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SleepingExtrasPayload.ID, (payload, context) -> context.server().execute(() ->
                {
                    if (RuntimeController.getCurrentTParams().sleepingExtras()) {
                        if (context.player() instanceof ServerPlayerEntity serverPlayerEntity) {
                            if (serverPlayerEntity.isSleeping()) {
                                if (serverPlayerEntity.getWorld().isDay()) {
                                    serverPlayerEntity.sendMessage(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW.getMessage(), true);
                                } else {
                                    serverPlayerEntity.getDataTracker().set(PlayerEntityUtil.IS_REALLY_SLEEPING, true);
                                    context.responseSender().sendPacket(new SleepingExtrasPayload(Unit.INSTANCE));
                                    Criteria.SLEPT_IN_BED.trigger(context.player());
                                    if (!context.player().getServerWorld().isSleepingEnabled()) {
                                        context.player().sendMessage(Text.translatable("sleep.not_possible"), true);
                                    }
                                    ((ServerWorld) serverPlayerEntity.getWorld()).updateSleepingPlayers();
                                }
                            }
                        }
                    }
                }));
    }
}
