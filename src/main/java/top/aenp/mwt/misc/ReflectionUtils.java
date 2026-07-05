package top.aenp.mwt.misc;

import com.google.common.collect.ImmutableMap;
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
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ReflectionUtils {
    public static final ReflectedField<FireballEntity, Integer> FireballEntity$explosionPower = new ReflectedField<>(FireballEntity.class, Integer.class, "explosionPower", "field_7624");
    public static final ReflectedField<PersistentProjectileEntity, ItemStack> PersistentProjectileEntity$stack = new ReflectedField<>(PersistentProjectileEntity.class, ItemStack.class, "stack", "field_46970");
    public static final ReflectedField<TridentEntity, TrackedData<Boolean>> TridentEntity$ENCHANTED = new ReflectedField<>(TridentEntity.class, (Class<TrackedData<Boolean>>) ((Class<?>) TrackedData.class), "ENCHANTED", "field_21514");
    public static final ReflectedField<DefaultAttributeRegistry, Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer>> DefaultAttributeRegistry$DEFAULT_ATTRIBUTE_REGISTRY = new ReflectedField<>(DefaultAttributeRegistry.class, (Class<Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer>>) (Class<?>) Map.class, "DEFAULT_ATTRIBUTE_REGISTRY", "field_23730");
    public static final ReflectedField<DefaultAttributeContainer, Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>> DefaultAttributeContainer$instances = new ReflectedField<>(DefaultAttributeContainer.class, (Class<Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>>) (Class<?>) Map.class, "instances", "field_23713");
    public static final ReflectedField<EntityAttributeInstance, Double> EntityAttributeInstance$baseValue = new ReflectedField<>(EntityAttributeInstance.class, Double.class, "baseValue", "field_23704");
    public static final ReflectedField<EntityAttributeInstance, Double> EntityAttributeInstance$value = new ReflectedField<>(EntityAttributeInstance.class, Double.class, "value", "field_23706");
    public static final ReflectedField<Item, Item> Item$recipeRemainder = new ReflectedField<>(Item.class, Item.class, "recipeRemainder", "field_8008");
    public static final ReflectedField<WardenEntity, TrackedData<Integer>> WardenEntity$ANGER = new ReflectedField<>(WardenEntity.class, (Class<TrackedData<Integer>>) (Class<?>) TrackedData.class, "ANGER", "field_38148");
    public static final ReflectedField<WardenEntity, WardenAngerManager> WardenEntity$angerManager = new ReflectedField<>(WardenEntity.class, WardenAngerManager.class, "angerManager", "field_38141");

    private static final ReflectedField<Item, ComponentMap> Item$components = new ReflectedField<>(Item.class, ComponentMap.class, "components", "field_49263");

    private static final Method SimpleComponentMap$map;

    static {
        try {
            SimpleComponentMap$map = DataComponentTypes.DEFAULT_ITEM_COMPONENTS.getClass().getDeclaredMethod(EnvironmentDetection.isYarn ? "map" : "comp_2440");
            SimpleComponentMap$map.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to get method object.", e);
        }
    }

    public static class ReflectedField<C, F> {
        private final Field containedField;
        private final Class<F> fieldType;
        private final boolean isStaticConstant;
        private final F cachedConstant;
        private static final ImmutableMap<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = new ImmutableMap.Builder<Class<?>, Class<?>>()
                .put(byte.class, Byte.class)
                .put(short.class, Short.class)
                .put(int.class, Integer.class)
                .put(long.class, Long.class)
                .put(float.class, Float.class)
                .put(double.class, Double.class)
                .put(boolean.class, Boolean.class)
                .put(char.class, Character.class)
                .build();

        public ReflectedField(@NotNull Class<C> fieldClass, @NotNull Class<F> fieldType, @NotNull String prettyName, @Nullable String intermediaryName) {
            String fieldName = EnvironmentDetection.isYarn || intermediaryName == null ? prettyName : intermediaryName;
            try {
                this.containedField = fieldClass.getDeclaredField(fieldName);
                this.fieldType = fieldType;
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(String.format("Unable to find field with name: %s, %s", prettyName, intermediaryName), e);
            }
            this.containedField.setAccessible(true);
            if (!getBoxedType(this.containedField).isAssignableFrom(fieldType)) {
                throw new IllegalArgumentException("Wrong field type! Check the generics type!");
            }
            int mod = this.containedField.getModifiers();
            if (Modifier.isFinal(mod) && Modifier.isStatic(mod)) {
                this.isStaticConstant = true;
                try {
                    this.cachedConstant = this.fieldType.cast(this.containedField.get(null));
                } catch (IllegalAccessException | ClassCastException e) {
                    throw new RuntimeException(String.format("Failed to get value of field: %s", this.containedField), e);
                }
            } else {
                this.isStaticConstant = false;
                this.cachedConstant = null;
            }
        }

        private static Class<?> getBoxedType(Field field) {
            Class<?> fieldType = field.getType();
            Class<?> mappedType = PRIMITIVE_TO_BOXED.get(fieldType);
            return mappedType == null ? fieldType : mappedType;
        }

        public void setFieldValue(C instance, F targetValue) {
            if (this.isStaticConstant) {
                throw new UnsupportedOperationException(String.format("Field %s is constant, modification not supported.", cachedConstant));
            }
            try {
                this.containedField.set(instance, targetValue);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(String.format("Failed to set value of field: %s", containedField), e);
            }
        }

        public F getFieldValue(C instance) {
            if (this.isStaticConstant) {
                return this.cachedConstant;
            }
            try {
                Object value = this.containedField.get(instance);
                Class<F> fieldType = this.fieldType;
                return fieldType.cast(value);
            } catch (IllegalAccessException | ClassCastException e) {
                throw new RuntimeException(String.format("Failed to get value of field: %s", containedField), e);
            }
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
}
