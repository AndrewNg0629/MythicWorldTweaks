package top.aenp.mwt.network.v2.interfaces;

import top.aenp.mwt.network.v2.payloads.SleepingStateUpdateS2CPayload;

public interface MwtClientPlayNetworkHandler extends MythicClientPlayNetworkHandler {
    default void mythicworldtweaks$onSleepingStateUpdate(SleepingStateUpdateS2CPayload payload) {
        throw new RuntimeException();
    }
}
