package online.andrew2007.mythic.network.payloads;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponsePayload;
import online.andrew2007.mythic.network.MythicNetwork;

import java.util.Set;

public record ValidationC2SPayload(String modVersion, Set<String> allModIds) implements LoginQueryResponsePayload {
    @Override
    public void write(PacketByteBuf buf) {
        String serialized = MythicNetwork.DEFAULT_GSON.toJson(this);
        buf.writeString(serialized);
    }
}
