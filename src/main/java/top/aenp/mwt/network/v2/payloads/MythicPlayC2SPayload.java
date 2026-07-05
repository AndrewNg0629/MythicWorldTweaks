package top.aenp.mwt.network.v2.payloads;

import net.minecraft.network.packet.CustomPayload;
import top.aenp.mwt.network.v2.injections.ServerPlayNetworkHandlerMethodInjections;

public interface MythicPlayC2SPayload extends CustomPayload {
    void handle(ServerPlayNetworkHandlerMethodInjections handler);
}
