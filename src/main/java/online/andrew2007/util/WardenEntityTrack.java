package online.andrew2007.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class WardenEntityTrack {
    private static final CopyOnWriteArrayList<WardenEntity> wardenEntityList = new CopyOnWriteArrayList<>();

    public static void registerEntity(WardenEntity entity) {
        if (!wardenEntityList.contains(entity)) {
            wardenEntityList.add(entity);
        }
    }

    public static void clearEntities() {
        wardenEntityList.clear();
    }

    public static void tick() {
        Iterator<WardenEntity> iterator = wardenEntityList.iterator();
        WardenEntity wardenEntity;
        while (iterator.hasNext()) {
            wardenEntity = iterator.next();
            if (wardenEntity != null) {
                if (parseRemovalReason(wardenEntity.getRemovalReason())) {
                    wardenEntityList.remove(wardenEntity);
                }
            }
        }
    }

    public static void wardenRefresh() {
        Iterator<WardenEntity> iterator = wardenEntityList.iterator(); //todo
        WardenEntity wardenEntity;
        while (iterator.hasNext()) {
            wardenEntity = iterator.next();
            if (wardenEntity != null) {
                World world = wardenEntity.getWorld();
                float healthRate = wardenEntity.getHealth() / wardenEntity.getMaxHealth();
                int anger = wardenEntity.getAnger();
                WardenAngerManager angerManager = wardenEntity.getAngerManager();
                WardenEntity newWardenEntity = wardenEntity.convertTo(EntityType.WARDEN, true);
                if (newWardenEntity != null) {
                    newWardenEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(newWardenEntity.getBlockPos()), SpawnReason.CONVERSION, null);
                    newWardenEntity.setHealth(newWardenEntity.getMaxHealth() * healthRate);
                    //newWardenEntity.dataTracker.set(WardenEntity.ANGER, anger);
                    //newWardenEntity.angerManager = angerManager;
                }
            }
        }
    }

    public static boolean parseRemovalReason(Entity.RemovalReason reason) {
        if (reason != null) {
            return reason.equals(Entity.RemovalReason.CHANGED_DIMENSION) || reason.equals(Entity.RemovalReason.KILLED) || reason.equals(Entity.RemovalReason.DISCARDED);
        } else {
            return false;
        }
    }
}
