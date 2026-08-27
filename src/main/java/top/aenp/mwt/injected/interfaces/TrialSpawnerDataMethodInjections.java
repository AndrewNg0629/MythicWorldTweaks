package top.aenp.mwt.injected.interfaces;

import org.apache.commons.lang3.NotImplementedException;

public interface TrialSpawnerDataMethodInjections {
    default long mythicworldtweaks$getCompletionTimestamp() {
        throw new NotImplementedException();
    }

    default void mythicworldtweaks$setCompletionTimestamp(long time) {
        throw new NotImplementedException();
    }
}
