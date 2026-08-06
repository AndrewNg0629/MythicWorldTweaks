package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;

public interface ServerPlayNetworkHandlerMethodInjections {
    default void mythicworldtweaks$onTrySleep() {
        throw new NotImplementedException();
    }
}
