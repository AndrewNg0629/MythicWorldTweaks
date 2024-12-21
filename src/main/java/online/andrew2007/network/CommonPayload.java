package online.andrew2007.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import online.andrew2007.MythicWorldTweaks;

public record CommonPayload(String serializedObject) implements CustomPayload {
    public static final Identifier COMMON_PAYLOAD_ID = Identifier.of(MythicWorldTweaks.MOD_ID, "common_payload");
    public static final CustomPayload.Id<CommonPayload> ID = new CustomPayload.Id<>(COMMON_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, CommonPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, CommonPayload::serializedObject, CommonPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
