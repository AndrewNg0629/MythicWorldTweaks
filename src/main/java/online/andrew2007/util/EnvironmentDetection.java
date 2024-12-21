package online.andrew2007.util;

public class EnvironmentDetection {
    private static final boolean isYarn;
    private static final boolean isPhyClient;

    static {
        isYarn = determineYarn();
        isPhyClient = determinePhyClient();
    }

    private static boolean determineYarn() {
        try {
            Class.forName("net.minecraft.world.World");
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

    public static boolean isYarn() {
        return isYarn;
    }

    public static boolean isPhyClient() {
        return isPhyClient;
    }
}