package top.aenp.mwt.misc;

import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class FireBallEntityManager {
    private static final ConcurrentHashMap<ExplosiveProjectileEntity, Integer> fireballEntityList = new ConcurrentHashMap<>();

    public static void registerEntity(ExplosiveProjectileEntity entity) {
        fireballEntityList.put(entity, 0);
    }

    public static void tick() {
        Iterator<ExplosiveProjectileEntity> iterator = fireballEntityList.keySet().iterator();
        ExplosiveProjectileEntity entity;
        while (iterator.hasNext()) {
            entity = iterator.next();
            if (entity != null) {
                if (entity.isRemoved()) {
                    fireballEntityList.remove(entity);
                } else {
                    if (fireballEntityList.get(entity) >= ConfigManager.getConfig().tweaks().valueTweaks().fireballAutoDiscarding().discardTicks()) {
                        entity.discard();
                        fireballEntityList.remove(entity);
                    } else {
                        fireballEntityList.put(entity, fireballEntityList.get(entity) + 1);
                    }
                }
            }
        }
    }
}
