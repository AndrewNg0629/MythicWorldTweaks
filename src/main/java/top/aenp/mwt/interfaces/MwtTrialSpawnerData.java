package top.aenp.mwt.interfaces;

public interface MwtTrialSpawnerData {
    default long mythicworldtweaks$getCompletionTimestamp() {
        throw new RuntimeException();
    }

    default void mythicworldtweaks$setCompletionTimestamp(long time) {
        throw new RuntimeException();
    }
}
