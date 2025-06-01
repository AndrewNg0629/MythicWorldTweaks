package online.andrew2007.mythic.injectedInterfaces;

public interface PlayerEntityMethodInjections {
    default boolean mythicWorldTweaks$isFake() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default boolean mythicWorldTweaks$isUnderFallProtection() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default void mythicWorldTweaks$setUnderFallProtection(boolean value) {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default boolean mythicWorldTweaks$isReallySleeping() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default void mythicWorldTweaks$setReallySleeping(boolean value) {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
