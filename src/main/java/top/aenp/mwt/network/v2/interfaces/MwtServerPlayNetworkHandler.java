package top.aenp.mwt.network.v2.interfaces;

import top.aenp.mwl.network.v2.interfaces.MythicServerPlayNetworkHandler;

public interface MwtServerPlayNetworkHandler extends MythicServerPlayNetworkHandler {
    default void mythicworldtweaks$onTrySleep() {
        throw new RuntimeException();
    }
}
