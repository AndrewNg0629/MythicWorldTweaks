package online.andrew2007.mythic.util.client;

import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SleepingScreenTracker {
    private static final ConcurrentHashMap<SleepingChatScreen, Optional<ButtonWidget>> buttonMapA = new ConcurrentHashMap<>();
    public static void onCreate(SleepingChatScreen screen) {
        buttonMapA.put(screen, Optional.empty());
    }
    public static void onClose(SleepingChatScreen screen) {
        buttonMapA.remove(screen);
    }
    public static ButtonWidget getButtonA(SleepingChatScreen screen) {
        Optional<ButtonWidget> optional = buttonMapA.get(screen);
        if (optional == null) {
            return null;
        } else {
            return optional.orElse(null);
        }
    }
    public static void setButtonA(SleepingChatScreen screen, ButtonWidget button) {
        if (buttonMapA.containsKey(screen)) {
            if (button == null) {
                buttonMapA.put(screen, Optional.empty());
            } else {
                buttonMapA.put(screen, Optional.of(button));
            }
        }
    }
}
