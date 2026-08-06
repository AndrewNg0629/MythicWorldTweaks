package top.aenp.mwt.network.v2.payloads.interfaces;

import net.minecraft.network.packet.CustomPayload;
import top.aenp.mwt.network.v2.injections.ClientPlayNetworkHandlerMethodInjections;

public interface MythicPlayS2CPayload extends CustomPayload {
    void handle(ClientPlayNetworkHandlerMethodInjections handler);
}
