package top.aenp.mwt.injected.interfaces;

import org.apache.commons.lang3.NotImplementedException;

public interface TrialSpawnerDataMethodInjections {
    default int mythicworldtweaks$getCompletionElapsedTicks() {
        throw new NotImplementedException();
    }

    default void mythicworldtweaks$setCompletionElapsedTicks(int elapsedTicks) {
        throw new NotImplementedException();
    }
}
