package online.andrew2007.mythic.injectedInterfaces;

public interface ItemEntityMethodInjections {
    default boolean mythicWorldTweaks$isUnderProtection() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default void mythicWorldTweaks$setUnderProtection(boolean value) {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
