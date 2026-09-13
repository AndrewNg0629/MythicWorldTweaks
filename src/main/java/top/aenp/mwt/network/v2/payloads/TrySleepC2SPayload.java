package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.interfaces.MwtServerPlayNetworkHandler;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicPlayC2SPayload;
import top.aenp.mwt.network.v2.interfaces.MythicServerPlayNetworkHandler;

public class TrySleepC2SPayload implements MythicPlayC2SPayload {
    public static final CustomPayload.Id<TrySleepC2SPayload> ID = new Id<>(Identifier.of("mwt", "try_sleep"));
    public static final PacketCodec<PacketByteBuf, TrySleepC2SPayload> CODEC = new PacketCodec<>() {
        @Override
        public TrySleepC2SPayload decode(PacketByteBuf buf) {
            return new TrySleepC2SPayload();
        }

        @Override
        public void encode(PacketByteBuf buf, TrySleepC2SPayload value) {
        }
    };

    @Override
    public void handle(MythicServerPlayNetworkHandler handler) {
        ((MwtServerPlayNetworkHandler) handler).mythicworldtweaks$onTrySleep();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
