package online.andrew2007.mythic.util;

import net.minecraft.text.Text;
import online.andrew2007.mythic.MythicWorldTweaks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LocalToaster {
    private static Method toastMethod = null;
    private static boolean isToasterReady = false;

    public static void receiveToaster(Method method) {
        if (!isToasterReady) {
            isToasterReady = true;
            toastMethod = method;
        }
    }

    public static void toast(Text title, Text index) {
        if (EnvironmentDetection.isPhyClient) {
            if (isToasterReady) {
                try {
                    toastMethod.invoke(null, title, index);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    MythicWorldTweaks.LOGGER.error("Error toasting.", e);
                }
            } else {
                MythicWorldTweaks.LOGGER.error("Toaster is not ready.");
            }
        }
    }
}
