package top.aenp.mwt.misc;

import net.minecraft.text.Text;
import top.aenp.mwl.misc.EnvironmentDetector;
import top.aenp.mwt.MythicWorldTweaks;

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

    @SuppressWarnings("unused")
    public static void toast(Text title, Text index) {
        if (EnvironmentDetector.isPhyClient) {
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
