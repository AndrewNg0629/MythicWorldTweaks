package online.andrew2007.mythic.util;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerEntityUtil {
    static {
        Class<?> gotFPClass = null;
        try {
            gotFPClass = Class.forName("carpet.patches.EntityPlayerMPFake");
        } catch (ClassNotFoundException ignored) {}
        carpetFPClass = gotFPClass;
    }

    public static final ConcurrentHashMap<ServerPlayerEntity, Pair<Byte, Byte>> playerDoubleClickTime = new ConcurrentHashMap<>();
    public static final TrackedData<Boolean> IS_UNDER_FALL_PROTECTION = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_FAKE = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final Class<?> carpetFPClass;

    public static void sneakingDCCheck(ServerPlayerEntity player) {
        if (!playerDoubleClickTime.containsKey(player)) {
            playerDoubleClickTime.put(player, new Pair<>((byte) 0, (byte) -1));
        }
        Pair<Byte, Byte> pair = playerDoubleClickTime.get(player);
        byte pressTime = pair.getLeft();
        if (player.isSneaking()) {
            if (pressTime >= (byte) 0 && pressTime < (byte) 10) {
                pair.setLeft(++pressTime);
            } else {
                pair.setLeft((byte) -1);
            }
        } else {
            if (pressTime > (byte) 0 && pressTime <= (byte) 10) {
                if (pair.getRight() > (byte) 0) {
                    player.removeAllPassengers();
                }
                pair.setRight((byte) 0);
            }
            pair.setLeft((byte) 0);
        }
        byte singleClickInterval = pair.getRight();
        if (singleClickInterval >= (byte) 0) {
            if (singleClickInterval < (byte) 10) {
                pair.setRight(++singleClickInterval);
            } else {
                pair.setRight((byte) -1);
            }
        }
        playerDoubleClickTime.put(player, pair);
    }

    public static boolean determineFake(ServerPlayerEntity serverPlayerEntity) {
        if (carpetFPClass == null) {
            return false;
        } else {
            return carpetFPClass.isAssignableFrom(serverPlayerEntity.getClass());
        }
    }

    public static void staticInit() {}
}
