package top.aenp.mwt.network.v2.payloads.interfaces;

import net.minecraft.network.packet.CustomPayload;
import top.aenp.mwt.network.v2.interfaces.MythicServerPlayNetworkHandler;

public interface MythicPlayC2SPayload extends CustomPayload {
    void handle(MythicServerPlayNetworkHandler handler);
}
