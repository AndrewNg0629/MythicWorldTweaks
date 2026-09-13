package top.aenp.mwt.misc;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;
import top.aenp.mwl.misc.EnvironmentDetector;
import top.aenp.mwl.misc.MythicReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectionUtils {
    public static final MythicReflectionUtils.ReflectedField<FireballEntity, Integer> FireballEntity$explosionPower = new MythicReflectionUtils.ReflectedField<>(FireballEntity.class, Integer.class, "explosionPower", "field_7624");
    public static final MythicReflectionUtils.ReflectedField<PersistentProjectileEntity, ItemStack> PersistentProjectileEntity$stack = new MythicReflectionUtils.ReflectedField<>(PersistentProjectileEntity.class, ItemStack.class, "stack", "field_46970");
    public static final MythicReflectionUtils.ReflectedField<TridentEntity, TrackedData<Boolean>> TridentEntity$ENCHANTED = new MythicReflectionUtils.ReflectedField<>(TridentEntity.class, (Class<TrackedData<Boolean>>) ((Class<?>) TrackedData.class), "ENCHANTED", "field_21514");
    public static final MythicReflectionUtils.ReflectedField<DefaultAttributeRegistry, Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer>> DefaultAttributeRegistry$DEFAULT_ATTRIBUTE_REGISTRY = new MythicReflectionUtils.ReflectedField<>(DefaultAttributeRegistry.class, (Class<Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer>>) (Class<?>) Map.class, "DEFAULT_ATTRIBUTE_REGISTRY", "field_23730");
    public static final MythicReflectionUtils.ReflectedField<DefaultAttributeContainer, Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>> DefaultAttributeContainer$instances = new MythicReflectionUtils.ReflectedField<>(DefaultAttributeContainer.class, (Class<Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>>) (Class<?>) Map.class, "instances", "field_23713");
    public static final MythicReflectionUtils.ReflectedField<EntityAttributeInstance, Double> EntityAttributeInstance$baseValue = new MythicReflectionUtils.ReflectedField<>(EntityAttributeInstance.class, Double.class, "baseValue", "field_23704");
    public static final MythicReflectionUtils.ReflectedField<EntityAttributeInstance, Double> EntityAttributeInstance$value = new MythicReflectionUtils.ReflectedField<>(EntityAttributeInstance.class, Double.class, "value", "field_23706");
    public static final MythicReflectionUtils.ReflectedField<Item, Item> Item$recipeRemainder = new MythicReflectionUtils.ReflectedField<>(Item.class, Item.class, "recipeRemainder", "field_8008");
    public static final MythicReflectionUtils.ReflectedField<WardenEntity, TrackedData<Integer>> WardenEntity$ANGER = new MythicReflectionUtils.ReflectedField<>(WardenEntity.class, (Class<TrackedData<Integer>>) (Class<?>) TrackedData.class, "ANGER", "field_38148");
    public static final MythicReflectionUtils.ReflectedField<WardenEntity, WardenAngerManager> WardenEntity$angerManager = new MythicReflectionUtils.ReflectedField<>(WardenEntity.class, WardenAngerManager.class, "angerManager", "field_38141");
    public static final MythicReflectionUtils.ReflectedField<FeatureSet, Long> FeatureSet$featuresMask = new MythicReflectionUtils.ReflectedField<>(FeatureSet.class, Long.class, "featuresMask", "field_40175");
    public static final MythicReflectionUtils.ReflectedField<ServerLoginNetworkHandler, Enum<?>> ServerLoginNetworkHandler$state;
    private static final MythicReflectionUtils.ReflectedField<Item, ComponentMap> Item$components = new MythicReflectionUtils.ReflectedField<>(Item.class, ComponentMap.class, "components", "field_49263");
    private static final Class<Enum<?>> ServerLoginNetworkHandler$State;
    private static final Enum<?>[] serverLoginStates;
    private static final Method SimpleComponentMap$map;
    private static final Method LoginQueryRequestS2CPacket$readPayload0;
    private static final Method LoginQueryResponseC2SPacket$readPayload0;

    static {
        try {
            SimpleComponentMap$map = DataComponentTypes.DEFAULT_ITEM_COMPONENTS.getClass().getDeclaredMethod(EnvironmentDetector.isYarn ? "map" : "comp_2440");
            SimpleComponentMap$map.setAccessible(true);
            LoginQueryRequestS2CPacket$readPayload0 = LoginQueryRequestS2CPacket.class.getDeclaredMethod(EnvironmentDetector.isYarn ? "readPayload" : "method_52287", Identifier.class, PacketByteBuf.class);
            LoginQueryRequestS2CPacket$readPayload0.setAccessible(true);
            LoginQueryResponseC2SPacket$readPayload0 = LoginQueryResponseC2SPacket.class.getDeclaredMethod(EnvironmentDetector.isYarn ? "readPayload" : "method_52290", Integer.TYPE, PacketByteBuf.class);
            LoginQueryResponseC2SPacket$readPayload0.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to get method object.", e);
        }
        try {
            ServerLoginNetworkHandler$State = (Class<Enum<?>>) Class.forName(EnvironmentDetector.isYarn ? "net.minecraft.server.network.ServerLoginNetworkHandler$State" : "net.minecraft.class_3248$class_3249");
            ServerLoginNetworkHandler$state = new MythicReflectionUtils.ReflectedField<>(ServerLoginNetworkHandler.class, ServerLoginNetworkHandler$State, "state", "field_14163");
            serverLoginStates = ServerLoginNetworkHandler$State.getEnumConstants();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to get class.", e);
        }
    }

    public static Reference2ObjectMap<ComponentType<?>, Object> getItemComponentsUnderlyingMap(Item item) {
        Reference2ObjectMap<ComponentType<?>, Object> underlyingMap;
        ComponentMap componentMap = item.getComponents();
        try {
            underlyingMap = (Reference2ObjectMap<ComponentType<?>, Object>) SimpleComponentMap$map.invoke(componentMap);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get underlying map of item components.", e);
        }
        return underlyingMap;
    }

    public static void separateItemComponents(Item item) {
        ComponentMap copiedComponents = ComponentMap.builder().addAll(item.getComponents()).build();
        Item$components.setFieldValue(item, copiedComponents);
    }

    public static void setLoginHandlerState(ServerLoginNetworkHandler instance, int state) {
        ServerLoginNetworkHandler$state.setFieldValue(instance, serverLoginStates[state]);
    }

    public static boolean isHandlerNegotiating(ServerLoginNetworkHandler instance) {
        return Objects.equals(ServerLoginNetworkHandler$state.getFieldValue(instance), serverLoginStates[3]);
    }
}
