package top.aenp.mwt.injected.interfaces;

import org.apache.commons.lang3.NotImplementedException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface VaultBlockEntityMethodInjections {
    default ConcurrentHashMap<UUID, Long> mythicworldtweaks$getTimestampMap() {
        throw new NotImplementedException();
    }
}
