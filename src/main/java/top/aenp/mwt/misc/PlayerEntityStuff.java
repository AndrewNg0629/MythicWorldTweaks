package top.aenp.mwt.misc;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityStuff {
    public static final Class<?> carpetFPClass;

    static {
        Class<?> carpetFPClass0 = null;
        try {
            carpetFPClass0 = Class.forName("carpet.patches.EntityPlayerMPFake");
        } catch (ClassNotFoundException ignored) {
        }
        carpetFPClass = carpetFPClass0;
    }

    public static boolean determineFake(PlayerEntity playerEntity) {
        if (carpetFPClass == null) {
            return false;
        } else {
            return carpetFPClass.isAssignableFrom(playerEntity.getClass());
        }
    }
}
