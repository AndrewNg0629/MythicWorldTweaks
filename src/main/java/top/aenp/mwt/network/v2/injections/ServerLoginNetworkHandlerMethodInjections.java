package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;
import top.aenp.mwt.network.v2.test.TestLoginC2SPayload;

public interface ServerLoginNetworkHandlerMethodInjections {
    default void labmod$onTestLoginC2S(TestLoginC2SPayload payload) {
        throw new NotImplementedException();
    }
}
