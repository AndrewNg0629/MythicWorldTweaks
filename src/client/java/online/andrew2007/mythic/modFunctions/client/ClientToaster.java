package online.andrew2007.mythic.modFunctions.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.modFunctions.LocalToaster;

import java.lang.reflect.Method;

public class ClientToaster {
    private static MinecraftClient minecraftClient = null;
    private static boolean isToasterReady = false;

    public static void initToaster(MinecraftClient client) {
        if (!isToasterReady) {
            minecraftClient = client;
            isToasterReady = true;
            Method toastMethod;
            try {
                toastMethod = ClientToaster.class.getDeclaredMethod("toast", Text.class, Text.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            toastMethod.setAccessible(true);
            LocalToaster.receiveToaster(toastMethod);
        }
    }

    private static void toast(Text title, Text index) {
        if (isToasterReady) {
            minecraftClient.getToastManager().add(SystemToast.create(minecraftClient, SystemToast.Type.NARRATOR_TOGGLE, title, index));
        } else {
            MythicWorldTweaks.LOGGER.error("Toaster is not ready.");
        }
    }
}
