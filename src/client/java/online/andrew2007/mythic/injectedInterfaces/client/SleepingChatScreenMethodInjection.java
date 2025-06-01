package online.andrew2007.mythic.injectedInterfaces.client;

import net.minecraft.client.gui.widget.ButtonWidget;

public interface SleepingChatScreenMethodInjection {
    default ButtonWidget mythicWorldTweaks$getSleepButton() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
