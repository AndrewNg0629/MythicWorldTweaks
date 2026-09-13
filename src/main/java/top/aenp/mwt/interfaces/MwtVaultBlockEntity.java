package top.aenp.mwt.interfaces;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface MwtVaultBlockEntity {
    default ConcurrentHashMap<UUID, Long> mythicworldtweaks$getTimestampMap() {
        throw new RuntimeException();
    }
}
