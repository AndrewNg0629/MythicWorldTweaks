package top.aenp.mwt.network.v2.injections;

import org.apache.commons.lang3.NotImplementedException;
import top.aenp.mwt.config.v2.NetworkSyncedConfig;
import top.aenp.mwt.network.v2.test.TestLoginS2CPayload;

public interface ClientLoginNetworkHandlerMethodInjections {
    default void labmod$onTestLoginS2C(TestLoginS2CPayload payload) {
        throw new NotImplementedException();
    }

    default void labmod$onConfigPush(NetworkSyncedConfig config) {
        throw new NotImplementedException();
    }
}
