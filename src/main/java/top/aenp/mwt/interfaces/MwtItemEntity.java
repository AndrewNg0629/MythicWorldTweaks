package top.aenp.mwt.interfaces;

public interface MwtItemEntity {
    default boolean mythicWorldTweaks$isUnderProtection() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default void mythicWorldTweaks$setUnderProtection(boolean value) {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
