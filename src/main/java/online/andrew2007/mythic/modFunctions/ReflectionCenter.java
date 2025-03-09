package online.andrew2007.mythic.modFunctions;

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
public class ReflectionCenter {
    public static final FieldContainer<FireballEntity, Integer> explosionPower = new FieldContainer<>(FireballEntity.class, Integer.class, "explosionPower", "field_7624");
    public static final FieldContainer<PersistentProjectileEntity, ItemStack> stack = new FieldContainer<>(PersistentProjectileEntity.class, ItemStack.class, "stack", "field_46970");
    public static final FieldContainer<TridentEntity, TrackedData<Boolean>> ENCHANTED = new FieldContainer<>(TridentEntity.class, (Class<TrackedData<Boolean>>) ((Class<?>) TrackedData.class), "ENCHANTED", "field_21514");
    public static final FieldContainer<TridentEntity, Boolean> dealtDamage = new FieldContainer<>(TridentEntity.class, Boolean.class, "dealtDamage", "field_7648");
    public static final FieldContainer<DefaultAttributeRegistry, Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer>> DEFAULT_ATTRIBUTE_REGISTRY = new FieldContainer<>(DefaultAttributeRegistry.class, (Class<Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer>>) (Class<?>) Map.class, "DEFAULT_ATTRIBUTE_REGISTRY", "field_23730");
    public static final FieldContainer<DefaultAttributeContainer, Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>> instances = new FieldContainer<>(DefaultAttributeContainer.class, (Class<Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>>) (Class<?>) Map.class, "instances", "field_23713");
    public static final FieldContainer<EntityAttributeInstance, Double> baseValue = new FieldContainer<>(EntityAttributeInstance.class, Double.class, "baseValue", "field_23704");
    public static final FieldContainer<EntityAttributeInstance, Double> value = new FieldContainer<>(EntityAttributeInstance.class, Double.class, "value", "field_23706");
    public static final FieldContainer<Item, Item> recipeRemainder = new FieldContainer<>(Item.class, Item.class, "recipeRemainder", "field_8008");
    public static final FieldContainer<WardenEntity, TrackedData<Integer>> ANGER = new FieldContainer<>(WardenEntity.class, (Class<TrackedData<Integer>>) (Class<?>) TrackedData.class, "ANGER", "field_38148");
    public static final FieldContainer<WardenEntity, WardenAngerManager> angerManager = new FieldContainer<>(WardenEntity.class, WardenAngerManager.class, "angerManager", "field_38141");

    public static <C, F> F getFieldValue(FieldContainer<C, F> fieldContainer, C instance) {
        if (fieldContainer.isConstant) {
            return fieldContainer.cachedConstant;
        }
        try {
            Object value = fieldContainer.containedField.get(instance);
            Class<F> fieldType = fieldContainer.fieldType;
            return fieldType.cast(value);
        } catch (IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(String.format("Failed to get value of field: %s", fieldContainer.containedField), e);
        }
    }

    public static <C, F> void setFieldValue(FieldContainer<C, F> fieldContainer, C instance, F targetValue) {
        if (fieldContainer.isConstant) {
            throw new UnsupportedOperationException(String.format("Field %s is constant, modification not supported.", fieldContainer.cachedConstant));
        }
        try {
            fieldContainer.containedField.set(instance, targetValue);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(String.format("Failed to set value of field: %s", fieldContainer.containedField), e);
        }
    }

    public static class FieldContainer<C, F> {
        private final Field containedField;
        private final Class<F> fieldType;
        private final boolean isConstant;
        private final F cachedConstant;

        public FieldContainer(@NotNull Class<C> fieldClass, @NotNull Class<F> fieldType, @NotNull String prettyName, @Nullable String intermediaryName) {
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
                this.isConstant = true;
                try {
                    this.cachedConstant = this.fieldType.cast(this.containedField.get(null));
                } catch (IllegalAccessException | ClassCastException e) {
                    throw new RuntimeException(String.format("Failed to get value of field: %s", this.containedField), e);
                }
            } else {
                this.isConstant = false;
                this.cachedConstant = null;
            }
        }

        private static Class<?> getBoxedType(Field field) {
            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type == int.class) return Integer.class;
                if (type == boolean.class) return Boolean.class;
                if (type == byte.class) return Byte.class;
                if (type == char.class) return Character.class;
                if (type == short.class) return Short.class;
                if (type == long.class) return Long.class;
                if (type == float.class) return Float.class;
                if (type == double.class) return Double.class;
            }
            return type;
        }
    }

    public static class BoxedMethods {
        private static final Method map;

        static {
            try {
                map = DataComponentTypes.DEFAULT_ITEM_COMPONENTS.getClass().getDeclaredMethod(EnvironmentDetection.isYarn ? "map" : "comp_2440");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to get method object.", e);
            }
        }

        public static Reference2ObjectMap<ComponentType<?>, Object> map(@NotNull ComponentMap instance) {
            Reference2ObjectMap<ComponentType<?>, Object> result;
            try {
                map.setAccessible(true);
                result = (Reference2ObjectMap<ComponentType<?>, Object>) map.invoke(instance);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Failed to invoke method \"map\"", e);
            }
            return result;
        }
    }
}
