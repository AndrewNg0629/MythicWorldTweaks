package top.aenp.mwt.interfaces.client;

import net.minecraft.client.gui.widget.ButtonWidget;

public interface MwtSleepingChatScreen {
    default ButtonWidget mythicWorldTweaks$getSleepButton() {
        throw new UnsupportedOperationException("Method must be overridden to be used.");
    }
}
