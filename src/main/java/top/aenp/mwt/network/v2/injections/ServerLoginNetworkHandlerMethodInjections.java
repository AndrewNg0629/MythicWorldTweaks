package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;
import top.aenp.mwt.network.v2.payloads.LoginModIdListC2SPayload;
import top.aenp.mwt.network.v2.payloads.LoginModVersionC2SPayload;

public interface ServerLoginNetworkHandlerMethodInjections {
    default void mythicworldtweaks$onModVersion(LoginModVersionC2SPayload version) {
        throw new NotImplementedException();
    }

    default void mythicworldtweaks$onModIdList(LoginModIdListC2SPayload list) {
        throw new NotImplementedException();
    }
}
