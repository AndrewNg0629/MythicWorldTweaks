package online.andrew2007.mythic.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;

public record PlayConfigPushPayload(TransmittableRuntimeParams params) implements CustomPayload {
    public static final Identifier payloadIdentifier = Identifier.of(MythicWorldTweaks.MOD_ID, "play_config_push");
    public static final CustomPayload.Id<PlayConfigPushPayload> ID = new CustomPayload.Id<>(payloadIdentifier);
    private static final PacketCodec<ByteBuf, TransmittableRuntimeParams> PARAMS_CODEC = new PacketCodec<>() {
        @Override
        public TransmittableRuntimeParams decode(ByteBuf buf) {
            String serialized = StringEncoding.decode(buf, 32767);
            return TransmittableRuntimeParams.TRANSMITTING_GSON.fromJson(serialized, TransmittableRuntimeParams.class);
        }

        @Override
        public void encode(ByteBuf buf, TransmittableRuntimeParams value) {
            String serialized = TransmittableRuntimeParams.TRANSMITTING_GSON.toJson(value);
            StringEncoding.encode(buf, serialized, 32767);
        }
    };
    public static final PacketCodec<ByteBuf, PlayConfigPushPayload> CODEC = PacketCodec.tuple(PARAMS_CODEC, PlayConfigPushPayload::params, PlayConfigPushPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
