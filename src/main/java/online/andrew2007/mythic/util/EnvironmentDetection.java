package online.andrew2007.mythic.util;

public class EnvironmentDetection {
    public static final boolean isYarn;
    public static final boolean isPhyClient;

    static {
        isYarn = determineYarn();
        isPhyClient = determinePhyClient();
    }

    private static boolean determineYarn() {
        try {
            Class.forName("net.minecraft.MinecraftVersion");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean determinePhyClient() {
        if (isYarn) {
            try {
                Class.forName("net.minecraft.client.MinecraftClient");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        } else {
            try {
                Class.forName("net.minecraft.class_310");
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
    }
}