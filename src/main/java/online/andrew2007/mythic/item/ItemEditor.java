package online.andrew2007.mythic.item;

import com.mojang.datafixers.util.Unit;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import online.andrew2007.mythic.util.ReflectionCenter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ItemEditor {
    private final static HashMap<Item, ItemEditor> itemEditors = new HashMap<>();
    private final Item carriedItem;
    private final boolean itemDamageable;
    private final int vanillaMaxStackSize;
    private final int vanillaMaxDamage;
    private final Rarity vanillaRarity;
    private final FoodComponent vanillaFoodComponent;
    private final boolean vanillaFireResistance;
    private final Item vanillaRecipeRemainder;
    private int maxStackSize;
    private int maxDamage;
    private Rarity rarity;
    private FoodComponent foodComponent;
    private boolean fireResistance;
    private Item recipeRemainder;

    private ItemEditor(@NotNull Item item) {
        itemEditors.put(item, this);
        this.carriedItem = item;
        this.vanillaMaxStackSize = item.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
        this.vanillaMaxDamage = item.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
        this.vanillaRarity = item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
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
        this.foodComponent = vanillaFoodComponent;
        this.fireResistance = this.vanillaFireResistance;
        this.recipeRemainder = this.vanillaRecipeRemainder;
    }

    public void setMaxStackSize(int maxStackSize) {
        if (this.itemDamageable) {
            this.maxStackSize = 1;
        } else {
            this.maxStackSize = maxStackSize;
        }
    }

    public void setMaxDamage(int maxDamage) {
        if (this.itemDamageable) {
            this.maxDamage = maxDamage;
        }
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public void setFoodComponent(FoodComponent foodComponent) {
        this.foodComponent = foodComponent;
    }

    public void setFireResistance(boolean fireResistance) {
        this.fireResistance = fireResistance;
    }

    public void setRecipeRemainder(Item recipeRemainder) {
        this.recipeRemainder = recipeRemainder;
    }

    public void apply() {
        Reference2ObjectMap<ComponentType<?>, Object> underlyingMap = ReflectionCenter.BoxedMethods.map(this.carriedItem.getComponents());
        if (this.itemDamageable) {
            underlyingMap.put(DataComponentTypes.MAX_DAMAGE, this.maxDamage);
        } else {
            underlyingMap.put(DataComponentTypes.MAX_STACK_SIZE, this.maxStackSize);
        }
        underlyingMap.put(DataComponentTypes.RARITY, this.rarity);
        if (this.foodComponent == null) {
            underlyingMap.remove(DataComponentTypes.FOOD);
        } else {
            underlyingMap.put(DataComponentTypes.FOOD, this.foodComponent);
        }
        if (this.fireResistance) {
            underlyingMap.put(DataComponentTypes.FIRE_RESISTANT, Unit.INSTANCE);
        } else {
            underlyingMap.remove(DataComponentTypes.FIRE_RESISTANT);
        }
        ReflectionCenter.setFieldValue(ReflectionCenter.recipeRemainder, this.carriedItem, this.recipeRemainder);
    }
}
