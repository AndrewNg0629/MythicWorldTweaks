package online.andrew2007.util;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MiscUtil {
    public static final ConcurrentHashMap<ServerPlayerEntity, Pair<Byte, Byte>> playerDoubleClickTime = new ConcurrentHashMap<>();
    public static final TrackedData<Boolean> IS_UNDER_FALL_PROTECTION = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final ImmutableMap<Identifier, Item> items;

    static {
        HashMap<Identifier, Item> temp = new HashMap<>();
        for (Item item : Registries.ITEM) {
            temp.put(Registries.ITEM.getId(item), item);
        }
        items = ImmutableMap.copyOf(temp);
    }

    public static void registerMWTEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (MythicWorldTweaksToggle.FIREBALL_AGE_TRACK.isEnabled()) {
                FireBallEntityManager.tick();
            }
            WardenEntityTrack.tick();
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> WardenEntityTrack.clearEntities());
    }

    public static void modifyWardenAttributes(boolean weaken) {
        Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> wardenAttributes = ReflectionCenter.getFieldValue(ReflectionCenter.instances, ReflectionCenter.getFieldValue(ReflectionCenter.DEFAULT_ATTRIBUTE_REGISTRY, null).get(EntityType.WARDEN));
        if (weaken) {
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MAX_HEALTH, 100.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D);
        } else {
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_MAX_HEALTH, 500.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0D);
            modifyEntityDA(wardenAttributes, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5D);
        }
    }

    public static void modifyEntityDA(Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> attributeInstances, RegistryEntry<EntityAttribute> targetAttribute, double targetValue) {
        EntityAttributeInstance attributeInstance = attributeInstances.get(targetAttribute);
        if (attributeInstance != null) {
            ReflectionCenter.setFieldValue(ReflectionCenter.baseValue, attributeInstance, targetValue);
            ReflectionCenter.setFieldValue(ReflectionCenter.value, attributeInstance, targetValue);
        }
    }

    public static void sneakingCheck(ServerPlayerEntity player) {
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
                    player.removeAllPassengers(); //TODO Toggle
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

}
