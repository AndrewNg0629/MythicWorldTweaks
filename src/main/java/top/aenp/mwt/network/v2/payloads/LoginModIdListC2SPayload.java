package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.interfaces.MwtServerLoginNetworkHandler;
import top.aenp.mwt.network.v2.payloads.interfaces.MythicLoginC2SPayload;
import top.aenp.mwt.network.v2.interfaces.MythicServerLoginNetworkHandler;

import java.util.ArrayList;
import java.util.List;

public record LoginModIdListC2SPayload(List<String> modIdList) implements MythicLoginC2SPayload {
    public static final Identifier ID = Identifier.of("mwt", "mod_id_list_c2s");
    public static final PacketCodec<PacketByteBuf, LoginModIdListC2SPayload> CODEC = new PacketCodec<>() {
        @Override
        public LoginModIdListC2SPayload decode(PacketByteBuf buf) {
            int size = buf.readInt();
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                arrayList.add(buf.readString());
            }
            return new LoginModIdListC2SPayload(arrayList);
        }

        @Override
        public void encode(PacketByteBuf buf, LoginModIdListC2SPayload value) {
            int size = value.modIdList.size();
            buf.writeInt(size);
            for (int i = 0; i < size; i++) {
                buf.writeString(value.modIdList.get(i));
            }
        }
    };

    @Override
    public Identifier mythicId() {
        return ID;
    }

    @Override
    public void handle(MythicServerLoginNetworkHandler handler) {
        ((MwtServerLoginNetworkHandler) handler).mythicworldtweaks$onModIdList(this);
    }
}
