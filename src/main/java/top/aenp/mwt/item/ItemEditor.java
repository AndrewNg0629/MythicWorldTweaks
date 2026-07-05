package top.aenp.mwt.item;

import com.mojang.datafixers.util.Unit;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import top.aenp.mwt.config.v2.ModConfig;
import top.aenp.mwt.misc.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class ItemEditor {
    private final static HashMap<Item, ItemEditor> itemEditors = new HashMap<>();
    private final Item carriedItem;
    private final boolean itemDamageable;
    private final int vanillaMaxStackSize;
    private final int vanillaMaxDamage;
    private final Rarity vanillaRarity;
    private final boolean vanillaIsFood;
    private final FoodComponent vanillaFoodComponent;
    private final boolean vanillaFireResistance;
    private final Item vanillaRecipeRemainder;
    private int maxStackSize;
    private int maxDamage;
    private Rarity rarity;
    private boolean isFood;
    private FoodComponent foodComponent;
    private boolean fireResistance;
    private Item recipeRemainder;

    private ItemEditor(@NotNull Item item) {
        itemEditors.put(item, this);
        this.carriedItem = item;
        ReflectionUtils.separateItemComponents(this.carriedItem);
        this.vanillaMaxStackSize = item.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
        this.vanillaMaxDamage = item.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
        this.vanillaRarity = item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
        this.vanillaIsFood = item.getComponents().contains(DataComponentTypes.FOOD);
        this.vanillaFoodComponent = item.getComponents().getOrDefault(DataComponentTypes.FOOD, null);
        this.vanillaFireResistance = item.getComponents().contains(DataComponentTypes.FIRE_RESISTANT);
        this.vanillaRecipeRemainder = item.getRecipeRemainder();
        this.itemDamageable = this.carriedItem.getComponents().contains(DataComponentTypes.MAX_DAMAGE);
        this.revertVanilla();
    }

    public static ItemEditor getInstance(@NotNull Item item) {
        ItemEditor editor = itemEditors.get(item);
        if (editor == null) {
            editor = new ItemEditor(item);
        }
        return editor;
    }

    public void revertVanilla() {
        this.maxStackSize = this.vanillaMaxStackSize;
        this.maxDamage = this.vanillaMaxDamage;
        this.rarity = this.vanillaRarity;
        this.isFood = this.vanillaIsFood;
        this.foodComponent = vanillaFoodComponent;
        this.fireResistance = this.vanillaFireResistance;
        this.recipeRemainder = this.vanillaRecipeRemainder;
    }

    @Deprecated(forRemoval = true)
    public void setMaxStackSize(int maxStackSize) {
        if (this.itemDamageable) {
            this.maxStackSize = 1;
        } else {
            this.maxStackSize = maxStackSize;
        }
    }

    @Deprecated(forRemoval = true)
    public void setMaxDamage(int maxDamage) {
        if (this.itemDamageable) {
            this.maxDamage = maxDamage;
        }
    }

    @Deprecated(forRemoval = true)
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    @Deprecated(forRemoval = true)
    public void setFoodComponent(FoodComponent foodComponent) {
        this.foodComponent = foodComponent;
    }

    @Deprecated(forRemoval = true)
    public void setFireResistance(boolean fireResistance) {
        this.fireResistance = fireResistance;
    }

    @Deprecated(forRemoval = true)
    public void setRecipeRemainder(Item recipeRemainder) {
        this.recipeRemainder = recipeRemainder;
    }

    public void loadFromConfigUnit(ModConfig.ItemEditorConfig.ItemEditorUnit unit) {
        unit.maxStackSize().ifPresent(size -> this.maxStackSize = size);
        unit.durability().ifPresent(durability -> this.maxDamage = durability);
        unit.fireResistant().ifPresent(resistant -> this.fireResistance = resistant);
        unit.rarity().ifPresent(rarity -> this.rarity = rarity);
        unit.craftingRemainsEntry().ifPresent(remains -> this.recipeRemainder = remains.value());
        unit.isFood().ifPresent(isFood -> this.isFood = isFood);
        unit.wrappedFoodComponents().ifPresent(wrappedComponents -> this.foodComponent = wrappedComponents.createComponents());
    }

    public void apply() {
        Reference2ObjectMap<ComponentType<?>, Object> underlyingMap = ReflectionUtils.getItemComponentsUnderlyingMap(this.carriedItem);
        if (this.itemDamageable) {
            underlyingMap.put(DataComponentTypes.MAX_DAMAGE, this.maxDamage);
        } else {
            underlyingMap.put(DataComponentTypes.MAX_STACK_SIZE, this.maxStackSize);
        }
        underlyingMap.put(DataComponentTypes.RARITY, this.rarity);
        if (this.isFood) {
            underlyingMap.put(DataComponentTypes.FOOD, this.foodComponent);
        } else {
            underlyingMap.remove(DataComponentTypes.FOOD);
        }
        if (this.fireResistance) {
            underlyingMap.put(DataComponentTypes.FIRE_RESISTANT, Unit.INSTANCE);
        } else {
            underlyingMap.remove(DataComponentTypes.FIRE_RESISTANT);
        }
        ReflectionUtils.Item$recipeRemainder.setFieldValue(this.carriedItem, Objects.equals(this.recipeRemainder, Items.AIR) ? null : this.recipeRemainder);
    }
}
