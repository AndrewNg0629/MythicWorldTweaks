package online.andrew2007.mythic.config.configFileParser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import online.andrew2007.mythic.config.ConfigLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record ItemEditorConfig(
        boolean enabled,
        ItemEditorConfigUnit[] itemEditorConfigUnits
) {
    public static class Deserializer implements CustomJsonDeserializer<ItemEditorConfig> {
        @Override
        public ItemEditorConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            if (!jsonObject.keySet().equals(Set.of(
                    "enabled",
                    "item_edit_config_list"
            ))) {
                throw new JsonParseException("Wrong config structure, please have a check.");
            }
            boolean enabled = jsonObject.get("enabled").getAsBoolean();
            List<JsonElement> configUnitsJSONList = jsonObject.get("item_edit_config_list").getAsJsonArray().asList();
            ItemEditorConfigUnit[] itemEditorConfigUnits = new ItemEditorConfigUnit[configUnitsJSONList.size()];
            List<Item> editedItemList = new ArrayList<>();
            for (int i = 0; i < configUnitsJSONList.size(); i++) {
                ItemEditorConfigUnit unit = context.deserialize(configUnitsJSONList.get(i), ItemEditorConfigUnit.class);
                if (editedItemList.contains(unit.targetItem)) {
                    throw new JsonParseException(String.format("Duplicate edit for item %s in config.", unit.targetItem.toString()));
                } else {
                    editedItemList.add(unit.targetItem);
                }
                itemEditorConfigUnits[i] = unit;
            }
            return new ItemEditorConfig(enabled, itemEditorConfigUnits);
        }
    }
    public record ItemEditorConfigUnit(
            Item targetItem,
            int maxStackSize,
            int maxDamage,
            boolean fireResistant,
            Rarity rarity,
            Item recipeRemainder,
            FoodComponent foodProperty
    ) {
        public static class Deserializer implements CustomJsonDeserializer<ItemEditorConfigUnit> {
            @Override
            public ItemEditorConfigUnit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                if (!jsonObject.has("target_item") ||
                        !Set.of(
                        "target_item",
                        "max_stack_size",
                        "max_damage",
                        "fire_resistant",
                        "rarity",
                        "recipe_remainder",
                        "food_property"
                ).containsAll(jsonObject.keySet())) {
                    throw new JsonParseException("Wrong config structure, please have a check.");
                }
                Identifier targetItemIdentifier = parseIdentifier(jsonObject.get("target_item"));
                Item targetItem = getItem(targetItemIdentifier);
                boolean itemDamageable = targetItem.getComponents().contains(DataComponentTypes.MAX_DAMAGE);
                int maxStackSize = targetItem.getMaxCount();
                int maxDamage = targetItem.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
                boolean fireResistant = targetItem.getComponents().contains(DataComponentTypes.FIRE_RESISTANT);
                Rarity rarity = targetItem.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
                Item recipeRemainder = targetItem.getRecipeRemainder();
                FoodComponent foodProperty = targetItem.getComponents().getOrDefault(DataComponentTypes.FOOD, null);
                if (jsonObject.has("max_stack_size")) {
                    maxStackSize = jsonObject.get("max_stack_size").getAsInt();
                    if (maxStackSize <= 0) {
                        throw new JsonParseException("Wrong config value for \"max_stack_size\", positive value is required.");
                    }
                }
                if (jsonObject.has("max_damage")) {
                    maxDamage = jsonObject.get("max_damage").getAsInt();
                    if (maxDamage < 0) {
                        throw new JsonParseException("Wrong config value for \"max_damage\", non-negative value is required.");
                    }
                }
                if (jsonObject.has("fire_resistant")) {
                    fireResistant = jsonObject.get("fire_resistant").getAsBoolean();
                }
                if (jsonObject.has("rarity")) {
                    String rarityIdentity = jsonObject.get("rarity").getAsString().toLowerCase();
                    rarity = switch (rarityIdentity) {
                        case "common" -> Rarity.COMMON;
                        case "uncommon" -> Rarity.UNCOMMON;
                        case "rare" -> Rarity.RARE;
                        case "epic" -> Rarity.EPIC;
                        default -> throw new JsonParseException(String.format("No such rarity identity: %s", rarityIdentity));
                    };
                }
                if (jsonObject.has("recipe_remainder")) {
                    JsonElement recipeRemainderJSON = jsonObject.get("recipe_remainder");
                    if (recipeRemainderJSON.isJsonNull()) {
                        recipeRemainder = null;
                    } else {
                        recipeRemainder = getItem(parseIdentifier(jsonObject.get("recipe_remainder")));
                    }
                }
                if (jsonObject.has("food_property")) {
                    JsonElement foodPropertyJSON = jsonObject.get("food_property");
                    if (foodPropertyJSON.isJsonNull()) {
                        foodProperty = null;
                    } else {
                        foodProperty = ((FoodProperty) context.deserialize(foodPropertyJSON, FoodProperty.class)).foodComponent;
                    }
                }
                if (itemDamageable) {
                    if (maxStackSize > 1) {
                        throw new JsonParseException(String.format("Item \"%s\" is damageable in its vanilla settings. You cannot set \"max_stack_size\" to more than 1.", targetItemIdentifier));
                    }
                    if (maxDamage == 0) {
                        throw new JsonParseException(String.format("Item \"%s\" is damageable in its vanilla settings. You cannot set \"max_damage\" to or less than 0.", targetItemIdentifier));
                    }
                } else {
                    if (maxDamage > 0) {
                        throw new JsonParseException(String.format("Item \"%s\" is not damageable in its vanilla settings. You cannot set \"max_damage\" to more than 0.", targetItemIdentifier));
                    }
                }
                return new ItemEditorConfigUnit(
                        targetItem,
                        maxStackSize,
                        maxDamage,
                        fireResistant,
                        rarity,
                        recipeRemainder,
                        foodProperty
                );
            }
        }
    }
    public record FoodProperty(FoodComponent foodComponent) {
        public static class Deserializer implements CustomJsonDeserializer<FoodProperty> {
            @Override
            public FoodProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                if (!jsonObject.keySet().equals(Set.of(
                        "nutrition",
                        "saturation",
                        "always_edible",
                        "eat_seconds",
                        "left_over_item",
                        "status_effects"
                ))) {
                    throw new JsonParseException("Wrong config structure, please have a check.");
                }
                int nutrition = jsonObject.get("nutrition").getAsInt();
                float saturation = jsonObject.get("saturation").getAsFloat();
                boolean alwaysEdible = jsonObject.get("always_edible").getAsBoolean();
                float eatSeconds = jsonObject.get("eat_seconds").getAsFloat();
                JsonElement leftOverItemJSON = jsonObject.get("left_over_item");
                Optional<ItemStack> leftOverItemOptional = Optional.empty();
                if (!leftOverItemJSON.isJsonNull()) {
                    Identifier leftOverItemIdentifier = parseIdentifier(leftOverItemJSON);
                    Item leftOverItem = getItem(leftOverItemIdentifier);
                    leftOverItemOptional = Optional.of(new ItemStack(leftOverItem));
                }
                List<FoodComponent.StatusEffectEntry> statusEffectEntryList = new ArrayList<>();
                if (nutrition < 0) {
                    throw new JsonParseException("Wrong config value for \"nutrition\", non-negative value is required.");
                } else if (saturation < 0) {
                    throw new JsonParseException("Wrong config value for \"saturation\", non-negative value is required.");
                } else if (eatSeconds < 0) {
                    throw new JsonParseException("Wrong config value for \"eat_seconds\", non-negative value is required.");
                }
                List<JsonElement> statusEffectsJSONList = jsonObject.get("status_effects").getAsJsonArray().asList();
                FoodStatusEffectUnit[] foodStatusEffectUnits = new FoodStatusEffectUnit[statusEffectsJSONList.size()];
                for (int i = 0; i < statusEffectsJSONList.size(); i++) {
                    foodStatusEffectUnits[i] = context.deserialize(statusEffectsJSONList.get(i), FoodStatusEffectUnit.class);
                }
                for (FoodStatusEffectUnit foodStatusEffectUnit : foodStatusEffectUnits) {
                    statusEffectEntryList.add(new FoodComponent.StatusEffectEntry(
                            new StatusEffectInstance(
                                    foodStatusEffectUnit.statusEffect(),
                                    foodStatusEffectUnit.lastingTime(),
                                    foodStatusEffectUnit.level() - 1),
                            foodStatusEffectUnit.probability()
                            ));
                }
                return new FoodProperty(new FoodComponent(nutrition, saturation, alwaysEdible, eatSeconds, leftOverItemOptional, statusEffectEntryList));
            }
        }
    }
    public record FoodStatusEffectUnit(RegistryEntry<StatusEffect> statusEffect, int level, int lastingTime, float probability) {
        public static class Deserializer implements CustomJsonDeserializer<FoodStatusEffectUnit> {
            @Override
            public FoodStatusEffectUnit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                if (!jsonObject.keySet().equals(Set.of("status_effect", "level", "lasting_time", "probability"))) {
                    throw new JsonParseException("Wrong config structure, please have a check.");
                }
                Identifier effectIdentifier = parseIdentifier(jsonObject.get("status_effect"));
                RegistryEntry<StatusEffect> statusEffect = ConfigLoader.getAllStatusEffects().get(effectIdentifier);
                if (statusEffect == null) {
                    throw new JsonParseException(String.format("Status effect \"%s\" doesn't exist!", effectIdentifier));
                }
                int level = jsonObject.get("level").getAsInt();
                int lastingTime = jsonObject.get("lasting_time").getAsInt();
                float probability = jsonObject.get("probability").getAsFloat();
                if (level <= 0 || level > 256) {
                    throw new JsonParseException("Wrong config value for \"level\", value from 1 to 256 is required.");
                } else if (lastingTime < -1) {
                    throw new JsonParseException("Wrong config value for \"lasting_time\", value equal to or greater than -1 is required.");
                } else if (probability < 0F || probability > 1F) {
                    throw new JsonParseException("Wrong config value for \"probability\", value from 0.0 to 1.0 is required.");
                }
                return new FoodStatusEffectUnit(
                        statusEffect,
                        level,
                        lastingTime,
                        probability
                );
            }
        }
    }
}
