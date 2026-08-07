package top.aenp.mwt.item;

import com.mojang.datafixers.util.Unit;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;
import top.aenp.mwt.misc.ReflectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class ItemEditor {
    private final static HashMap<Item, ItemEditor> itemEditors = new HashMap<>();
    private static final LinkedList<ItemEditor> effectiveEditors = new LinkedList<>();
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

    private ItemEditor(Item item) {
        itemEditors.put(item, this);
        carriedItem = item;
        ReflectionUtils.separateItemComponents(carriedItem);
        vanillaMaxStackSize = item.getComponents().getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1);
        vanillaMaxDamage = item.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
        vanillaRarity = item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
        vanillaIsFood = item.getComponents().contains(DataComponentTypes.FOOD);
        vanillaFoodComponent = item.getComponents().getOrDefault(DataComponentTypes.FOOD, null);
        vanillaFireResistance = item.getComponents().contains(DataComponentTypes.FIRE_RESISTANT);
        vanillaRecipeRemainder = item.getRecipeRemainder();
        itemDamageable = carriedItem.getComponents().contains(DataComponentTypes.MAX_DAMAGE);
        revertVanilla();
    }

    private static ItemEditor getInstance(Item item) {
        ItemEditor editor = itemEditors.get(item);
        if (editor == null) {
            editor = new ItemEditor(item);
        }
        return editor;
    }

    public static void applyFromModConfig() {
        for (ItemEditor effectiveEditors : effectiveEditors) {
            effectiveEditors.revertVanilla();
            effectiveEditors.applyEdits();
        }
        effectiveEditors.clear();
        for (ModConfig.ItemEditorConfig.ItemEditorUnit unit : ConfigManager.getConfig().itemEditorConfig().units()) {
            ItemEditor editor = getInstance(unit.itemEntry().value());
            effectiveEditors.add(editor);
            editor.loadFromConfigUnit(unit);
            editor.applyEdits();
        }
    }

    private void revertVanilla() {
        maxStackSize = vanillaMaxStackSize;
        maxDamage = vanillaMaxDamage;
        rarity = vanillaRarity;
        isFood = vanillaIsFood;
        foodComponent = vanillaFoodComponent;
        fireResistance = vanillaFireResistance;
        recipeRemainder = vanillaRecipeRemainder;
    }

    private void loadFromConfigUnit(ModConfig.ItemEditorConfig.ItemEditorUnit unit) {
        unit.maxStackSize().ifPresent(size -> maxStackSize = size);
        unit.durability().ifPresent(durability -> maxDamage = durability);
        unit.fireResistant().ifPresent(resistant -> fireResistance = resistant);
        unit.rarity().ifPresent(rarity -> this.rarity = rarity);
        unit.craftingRemainsEntry().ifPresent(remains -> recipeRemainder = remains.value());
        unit.isFood().ifPresent(isFood -> this.isFood = isFood);
        unit.wrappedFoodComponents().ifPresent(wrappedComponents -> foodComponent = wrappedComponents.createComponents());
    }

    private void applyEdits() {
        Reference2ObjectMap<ComponentType<?>, Object> underlyingMap = ReflectionUtils.getItemComponentsUnderlyingMap(carriedItem);
        if (itemDamageable) {
            underlyingMap.put(DataComponentTypes.MAX_DAMAGE, maxDamage);
        } else {
            underlyingMap.put(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
        }
        underlyingMap.put(DataComponentTypes.RARITY, rarity);
        if (isFood) {
            underlyingMap.put(DataComponentTypes.FOOD, foodComponent);
        } else {
            underlyingMap.remove(DataComponentTypes.FOOD);
        }
        if (fireResistance) {
            underlyingMap.put(DataComponentTypes.FIRE_RESISTANT, Unit.INSTANCE);
        } else {
            underlyingMap.remove(DataComponentTypes.FIRE_RESISTANT);
        }
        ReflectionUtils.Item$recipeRemainder.setFieldValue(carriedItem, Objects.equals(recipeRemainder, Items.AIR) ? null : recipeRemainder);
    }
}
