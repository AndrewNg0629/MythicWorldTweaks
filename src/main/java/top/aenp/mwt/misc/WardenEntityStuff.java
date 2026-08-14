package top.aenp.mwt.misc;

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
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WardenEntityStuff {
    public static void modifyWardenAttributes() {
        Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> wardenAttributes = ReflectionUtils.DefaultAttributeContainer$instances.getFieldValue(ReflectionUtils.DefaultAttributeRegistry$DEFAULT_ATTRIBUTE_REGISTRY.getFieldValue(null).get(EntityType.WARDEN));
        modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MAX_HEALTH, ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().maxHealth());
        modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MOVEMENT_SPEED, ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().baseMovementSpeed());
        modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().knockbackResistance());
        modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_DAMAGE, ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().meleeAttackDamage());
        modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, ConfigManager.getConfig().tweaks().valueTweaks().wardenAttributesControl().meleeAttackKnockback());
    }

    private static void modifyEntityDA(Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> attributeInstances, RegistryEntry<EntityAttribute> targetAttribute, double targetValue) {
        EntityAttributeInstance attributeInstance = attributeInstances.get(targetAttribute);
        if (attributeInstance != null) {
            ReflectionUtils.EntityAttributeInstance$baseValue.setFieldValue(attributeInstance, targetValue);
            ReflectionUtils.EntityAttributeInstance$value.setFieldValue(attributeInstance, targetValue);
        }
    }

    public static class WardenEntityTracker {
        public static final WardenEntityTracker INSTANCE = new WardenEntityTracker();
        private final CopyOnWriteArrayList<WardenEntity> wardenEntityList = new CopyOnWriteArrayList<>();

        public static boolean shouldRemove(Entity.RemovalReason reason) {
            if (reason != null) {
                return reason.equals(Entity.RemovalReason.CHANGED_DIMENSION) || reason.equals(Entity.RemovalReason.KILLED) || reason.equals(Entity.RemovalReason.DISCARDED);
            } else {
                return false;
            }
        }

        public void registerEntity(WardenEntity entity) {
            if (!wardenEntityList.contains(entity)) {
                wardenEntityList.add(entity);
            }
        }

        public void clearEntities() {
            wardenEntityList.clear();
        }

        public void tick() {
            Iterator<WardenEntity> iterator = wardenEntityList.iterator();
            WardenEntity wardenEntity;
            while (iterator.hasNext()) {
                wardenEntity = iterator.next();
                if (wardenEntity != null) {
                    if (shouldRemove(wardenEntity.getRemovalReason())) {
                        wardenEntityList.remove(wardenEntity);
                    }
                }
            }
        }

        public void refreshWardens() {
            for (WardenEntity wardenEntity : wardenEntityList) {
                if (wardenEntity != null) {
                    World world = wardenEntity.getWorld();
                    int anger = wardenEntity.getAnger();
                    WardenAngerManager angerManager = wardenEntity.getAngerManager();
                    WardenEntity newWardenEntity = wardenEntity.convertTo(EntityType.WARDEN, true);
                    if (newWardenEntity != null) {
                        newWardenEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(newWardenEntity.getBlockPos()), SpawnReason.CONVERSION, null);
                        newWardenEntity.setHealth(wardenEntity.getHealth());
                        newWardenEntity.getDataTracker().set(ReflectionUtils.WardenEntity$ANGER.getFieldValue(null), anger);
                        ReflectionUtils.WardenEntity$angerManager.setFieldValue(newWardenEntity, angerManager);
                    }
                }
            }
        }
    }

}
