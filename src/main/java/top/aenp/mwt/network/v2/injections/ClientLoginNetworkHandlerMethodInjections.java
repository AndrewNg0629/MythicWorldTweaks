package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;
import top.aenp.mwt.network.v2.payloads.LoginModVersionS2CPayload;


public interface ClientLoginNetworkHandlerMethodInjections {
    default void mythicworldtweaks$onModVersion(LoginModVersionS2CPayload version) {
        throw new NotImplementedException();
    }

    default void mythicworldtweaks$onModIdRequest() {
        throw new NotImplementedException();
    }
}
