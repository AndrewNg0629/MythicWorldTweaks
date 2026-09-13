package top.aenp.mwt.network.v2.interfaces;

public interface MwtServerPlayNetworkHandler extends MythicServerPlayNetworkHandler {
    default void mythicworldtweaks$onTrySleep() {
        throw new RuntimeException();
    }
}
