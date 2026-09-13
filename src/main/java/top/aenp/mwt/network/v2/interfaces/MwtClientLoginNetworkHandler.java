package top.aenp.mwt.network.v2.interfaces;

import top.aenp.mwt.network.v2.payloads.LoginModVersionS2CPayload;

public interface MwtClientLoginNetworkHandler extends MythicClientLoginNetworkHandler {
    default void mythicworldtweaks$onModVersion(LoginModVersionS2CPayload version) {
        throw new RuntimeException();
    }

    default void mythicworldtweaks$onModIdRequest() {
        throw new RuntimeException();
    }
}
