package top.aenp.mwt.network.v2.interfaces;

import top.aenp.mwt.network.v2.payloads.LoginModIdListC2SPayload;
import top.aenp.mwt.network.v2.payloads.LoginModVersionC2SPayload;

public interface MwtServerLoginNetworkHandler extends MythicServerLoginNetworkHandler {
    default void mythicworldtweaks$onModVersion(LoginModVersionC2SPayload version) {
        throw new RuntimeException();
    }

    default void mythicworldtweaks$onModIdList(LoginModIdListC2SPayload list) {
        throw new RuntimeException();
    }
}
