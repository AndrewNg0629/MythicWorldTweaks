package online.andrew2007.mythic.network;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.PlayerEntityStuff;
import online.andrew2007.mythic.network.payloads.PlayConfigPushPayload;
import online.andrew2007.mythic.network.payloads.SleepingExtrasPayload;

import java.util.concurrent.ConcurrentHashMap;

public class MythicNetwork {
    public static final PacketCodec<ByteBuf, Unit> UNIT_CODEC = new PacketCodec<>() {
        public Unit decode(ByteBuf byteBuf) {
            return Unit.INSTANCE;
        }

        public void encode(ByteBuf byteBuf, Unit unit) {
        }
    };
    public static final Gson DEFAULT_GSON = new Gson();
    private static final ConcurrentHashMap<Integer, Identifier> requestRegistry = new ConcurrentHashMap<>();
    private static int currentLoginQueryId = 99999;

    public static int registerRequest(Identifier payloadId) {
        if (currentLoginQueryId == Integer.MAX_VALUE) {
            currentLoginQueryId = 99999;
        }
        int queryId = currentLoginQueryId++;
        requestRegistry.put(queryId, payloadId);
        return queryId;
    }

    public static Identifier getResponseId(int queryId) {
        Identifier responseId = requestRegistry.get(queryId);
        if (responseId != null) {
            requestRegistry.remove(queryId);
            return responseId;
        } else {
            throw new IllegalStateException(String.format("No request matching query ID %s was found. Maybe it was not registered or it has been removed.", queryId));
        }
    }

    public static boolean isMythicResponse(int queryId) {
        return requestRegistry.containsKey(queryId);
    }

    @SuppressWarnings("resource") //Would you like to close the server?
    public static void commonInitialization() {
        PayloadTypeRegistry.playS2C().register(SleepingExtrasPayload.ID, SleepingExtrasPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SleepingExtrasPayload.ID, SleepingExtrasPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayConfigPushPayload.ID, PlayConfigPushPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayConfigPushPayload.ID, PlayConfigPushPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SleepingExtrasPayload.ID, (payload, context) -> context.server().execute(() ->
        {
            if (RuntimeController.getCurrentTParams().sleepingExtras()) {
                if (context.player() instanceof ServerPlayerEntity serverPlayerEntity) {
                    if (serverPlayerEntity.isSleeping()) {
                        if (serverPlayerEntity.getWorld().isDay()) {
                            serverPlayerEntity.sendMessage(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW.getMessage(), true);
                        } else {
                            serverPlayerEntity.getDataTracker().set(PlayerEntityStuff.IS_REALLY_SLEEPING, true);
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
        ServerPlayNetworking.registerGlobalReceiver(PlayConfigPushPayload.ID, (payload, context) -> context.server().execute(() -> PlayConfigPushValidator.onConfigPushResponse(context.player(), payload.params())));
    }
}
