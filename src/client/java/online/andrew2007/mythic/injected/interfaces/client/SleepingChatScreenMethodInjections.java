package online.andrew2007.mythic.injected.interfaces.client;

import net.minecraft.client.gui.widget.ButtonWidget;

public interface SleepingChatScreenMethodInjections {
    default ButtonWidget mythicWorldTweaks$getSleepButton() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
