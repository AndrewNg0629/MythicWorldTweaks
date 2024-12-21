package online.andrew2007.network;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;

public class PacketTypes {
    private static final HashMap<Integer, PacketType<?>> packetTypes = new HashMap<>();
    private static int typeInitOrdinal = 0;

    public static PacketType<?> getPacketType(int ordinal) {
        PacketType<?> packetType = packetTypes.get(ordinal);
        if (packetType == null) {
            throw new IllegalArgumentException(String.format("Packet type with ordinal %s was not found!", ordinal));
        }
        return packetType;
    }

    public static abstract class PacketType<P> {
        public final int typeOrdinal;
        public final Class<P> payloadType;
        public final Consumer<P> packetAction;

        public PacketType(@Nullable Class<P> payloadType, @Nullable Consumer<P> packetAction) {
            packetTypes.put(typeInitOrdinal, this);
            this.typeOrdinal = typeInitOrdinal;
            this.payloadType = payloadType;
            this.packetAction = packetAction;
            ++typeInitOrdinal;
        }
    }

    public static class S2CPacketType<P> extends PacketType<P> {
        public S2CPacketType(@Nullable Class<P> payloadType, @Nullable Consumer<P> packetAction) {
            super(payloadType, packetAction);
        }
    }

    public static class C2SPacketType<P> extends PacketType<P> {
        public C2SPacketType(@Nullable Class<P> payloadType, @Nullable Consumer<P> packetAction) {
            super(payloadType, packetAction);
        }
    }

}
