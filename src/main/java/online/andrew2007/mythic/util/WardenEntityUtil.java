package online.andrew2007.mythic.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import online.andrew2007.mythic.config.RuntimeController;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WardenEntityUtil {
    public static class WardenEntityTrack {
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
            Iterator<WardenEntity> iterator = wardenEntityList.iterator();
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
                        newWardenEntity.getDataTracker().set(ReflectionCenter.getFieldValue(ReflectionCenter.ANGER, null), anger);
                        ReflectionCenter.setFieldValue(ReflectionCenter.angerManager, newWardenEntity, angerManager);
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

    public static void modifyWardenAttributes() {
        Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> wardenAttributes = ReflectionCenter.getFieldValue(ReflectionCenter.instances, ReflectionCenter.getFieldValue(ReflectionCenter.DEFAULT_ATTRIBUTE_REGISTRY, null).get(EntityType.WARDEN));
        if (RuntimeController.getCurrentTParams().wardenAttributesWeakeningEnabled()) {
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MAX_HEALTH, RuntimeController.getCurrentTParams().wardenMaxHealth());
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, RuntimeController.getCurrentTParams().wardenKnockBackResistance());
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_DAMAGE, RuntimeController.getCurrentTParams().wardenMeleeAttackDamage());
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, RuntimeController.getCurrentTParams().wardenMeleeAttackKnockBack());
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MOVEMENT_SPEED, RuntimeController.getCurrentTParams().wardenIdleMovementSpeed());
        } else {
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MAX_HEALTH, 500.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F);
        }
    }

    private static void modifyEntityDA(Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> attributeInstances, RegistryEntry<EntityAttribute> targetAttribute, double targetValue) {
        EntityAttributeInstance attributeInstance = attributeInstances.get(targetAttribute);
        if (attributeInstance != null) {
            ReflectionCenter.setFieldValue(ReflectionCenter.baseValue, attributeInstance, targetValue);
            ReflectionCenter.setFieldValue(ReflectionCenter.value, attributeInstance, targetValue);
        }
    }

}
