package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;
import top.aenp.mwt.network.v2.payloads.SleepingStateUpdateS2CPayload;

public interface ClientPlayNetworkHandlerMethodInjections {
    default void mythicworldtweaks$onSleepingStateUpdate(SleepingStateUpdateS2CPayload payload) {
        throw new NotImplementedException();
    }
}
