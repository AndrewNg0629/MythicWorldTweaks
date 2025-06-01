package online.andrew2007.mythic.injectedInterfaces;

import online.andrew2007.mythic.config.runtimeParams.TransmittableRuntimeParams;

public interface ServerPlayerEntityMethodInjections {
    default void mythicWorldTweaks$onPlayConfigPush() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }

    default void mythicWorldTweaks$onPlayConfigPushResponse(TransmittableRuntimeParams params) {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
