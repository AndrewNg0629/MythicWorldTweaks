package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;
import top.aenp.mwt.network.v2.test.TestPlayC2SPayload;

public interface ServerPlayNetworkHandlerMethodInjections {
    default void labmod$onCustomC2S(TestPlayC2SPayload payload) {
        throw new NotImplementedException();
    }
}
