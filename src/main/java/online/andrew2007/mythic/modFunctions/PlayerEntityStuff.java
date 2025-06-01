package online.andrew2007.mythic.modFunctions;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityStuff {
    public static final Class<?> carpetFPClass;

    static {
        Class<?> gotFPClass = null;
        try {
            gotFPClass = Class.forName("carpet.patches.EntityPlayerMPFake");
        } catch (ClassNotFoundException ignored) {
        }
        carpetFPClass = gotFPClass;
    }

    public static boolean determineFake(PlayerEntity playerEntity) {
        if (carpetFPClass == null) {
            return false;
        } else {
            return carpetFPClass.isAssignableFrom(playerEntity.getClass());
        }
    }
}
