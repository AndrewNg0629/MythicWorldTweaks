package online.andrew2007.mythic.network.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.network.MWTNetwork;

public record SleepingExtrasPayload(Unit unit) implements CustomPayload {
    public static final Identifier packetIdentifier = Identifier.of(MythicWorldTweaks.MOD_ID, "player_sleep_payload");
    public static final CustomPayload.Id<SleepingExtrasPayload> ID = new CustomPayload.Id<>(packetIdentifier);
    public static final PacketCodec<RegistryByteBuf, SleepingExtrasPayload> CODEC = PacketCodec.tuple(MWTNetwork.UNIT_CODEC, SleepingExtrasPayload::unit, SleepingExtrasPayload::new);
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
