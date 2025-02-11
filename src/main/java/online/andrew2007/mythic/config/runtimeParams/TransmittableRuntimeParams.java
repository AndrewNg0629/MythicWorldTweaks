package online.andrew2007.mythic.config.runtimeParams;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import online.andrew2007.mythic.config.ConfigLoader;
import online.andrew2007.mythic.config.configFileParser.CustomJsonDeserializer;
import online.andrew2007.mythic.item.ItemEditor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public record TransmittableRuntimeParams(
        boolean throwableFireCharge,
        boolean largeFireCharge,
        boolean editablePlayerData,
        boolean blocksFriendlyCreepers,
        boolean itemExplosionResistance,
        boolean nestableShulkerBoxes,
        boolean rideablePlayers,
        boolean playerRidingGestures,
        boolean playerRidingProtection,
        boolean dispensableTridents,
        boolean voidReturnableTrident,
        boolean multiTridentDamage,
        boolean persistentTridents,
        boolean silentWardens,
        boolean constantVillagerConversion,
        boolean fakePlayerSleepExclusion,
        boolean sleepingExtras,
        boolean autoDiscardingFireBallEnabled,
        boolean stuffedShulkerBoxStackLimitEnabled,
        boolean shulkerBoxNestingLimitEnabled,
        boolean wardenAttributesWeakeningEnabled,
        boolean wardenSonicBoomWeakeningEnabled,
        int fireBallMaxLifeTicks,
        int shulkerBoxMaxStackSize,
        int shulkerBoxMaxLayers,
        double wardenMaxHealth,
        double wardenKnockBackResistance,
        double wardenMeleeAttackDamage,
        double wardenMeleeAttackKnockBack,
        double wardenIdleMovementSpeed,
        float wardenChasingMovementSpeed,
        int wardenAttackIntervalTicks,
        float sonicBoomDamage,
        double sonicBoomKnockBackRate,
        int sonicBoomIntervalTicks,
        boolean itemEditorEnabled,
        TransmittableIECUnit[] itemEditorConfig
) {
    static {
        Field[] fields = TransmittableRuntimeParams.class.getDeclaredFields();
        ImmutableSet.Builder<Field> setBuilder = new ImmutableSet.Builder<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().isPrimitive() && !Modifier.isStatic(field.getModifiers())) {
                setBuilder.add(field);
            }
        }
        allPrimitiveFields = setBuilder.build();
    }

    public static final Gson TRANSMITTING_GSON = new GsonBuilder()
            .registerTypeAdapter(Item.class, new ItemTypeAdapter())
            .registerTypeAdapter(FoodComponent.class, new FoodComponentTypeAdapter())
            .registerTypeAdapter(ItemStack.class, new FoodItemStackTypeAdapter())
            .registerTypeAdapter(FoodComponent.StatusEffectEntry.class, new FoodStatusEffectEntryTypeAdapter())
            .registerTypeAdapter(StatusEffectInstance.class, new FoodStatusEffectInstanceTypeAdapter())
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
            .create();

    public static final ImmutableSet<Field> allPrimitiveFields;

    public static TransmittableRuntimeParams getDefaultInstance() {
        return new TransmittableRuntimeParams(
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                200,
                1,
                2,
                500.0D,
                1.0D,
                30.0D,
                1.5D,
                0.3D,
                1.2F,
                18,
                10.0F,
                1.0D,
                34,
                false,
                new TransmittableIECUnit[0]
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        for (Field field : allPrimitiveFields) {
            try {
                Object thisValue = field.get(this);
                Object otherValue = field.get(other);
                if (!thisValue.equals(otherValue)) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to compare two TRParams.", e);
            }
        }
        if (this.itemEditorConfig.length != ((TransmittableRuntimeParams) other).itemEditorConfig.length) {
            return false;
        }
        HashMap<Item, TransmittableIECUnit> thisIECs = TransmittableIECUnit.arrayToMap(this.itemEditorConfig);
        HashMap<Item, TransmittableIECUnit> otherIECs = TransmittableIECUnit.arrayToMap(((TransmittableRuntimeParams) other).itemEditorConfig());
        for (Item item : thisIECs.keySet()) {
            if (!otherIECs.containsKey(item)) {
                return false;
            }
            TransmittableIECUnit thisUnit = thisIECs.get(item);
            TransmittableIECUnit otherUnit = otherIECs.get(item);
            if (!thisUnit.equals(otherUnit)) {
                return false;
            } else {
                otherIECs.remove(item);
            }
        }
        return otherIECs.isEmpty();
    }

    public record TransmittableIECUnit(
            Item targetItem,
            int maxStackSize,
            int maxDamage,
            boolean fireResistant,
            Rarity rarity,
            Item recipeRemainder,
            FoodComponent foodProperty
    ) {
        public static HashMap<Item, TransmittableIECUnit> arrayToMap(TransmittableIECUnit[] units) {
            HashMap<Item, TransmittableIECUnit> map = new HashMap<>();
            for (TransmittableIECUnit unit : units) {
                map.put(unit.targetItem, unit);
            }
            return map;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            if (this == other) return true;
            if (!this.getClass().equals(other.getClass())) return false;
            TransmittableIECUnit castedOther = (TransmittableIECUnit) other;
            boolean foodPropertyEquals = false;
            if (this.foodProperty == castedOther.foodProperty) {
                foodPropertyEquals = true;
            } else if (this.foodProperty != null && castedOther.foodProperty != null) {
                foodPropertyEquals = this.foodProperty.nutrition() == castedOther.foodProperty.nutrition() &&
                        this.foodProperty.saturation() == castedOther.foodProperty.saturation() &&
                        this.foodProperty.canAlwaysEat() == castedOther.foodProperty.canAlwaysEat() &&
                        this.foodProperty.eatSeconds() == castedOther.foodProperty.eatSeconds() &&
                        Objects.equals(this.foodProperty.usingConvertsTo().orElse(ItemStack.EMPTY).getItem(), castedOther.foodProperty.usingConvertsTo().orElse(ItemStack.EMPTY).getItem()) &&
                        Objects.equals(this.foodProperty.effects(), castedOther.foodProperty.effects());
            }
            return Objects.equals(this.targetItem, castedOther.targetItem) &&
                    this.maxStackSize == castedOther.maxStackSize &&
                    this.maxDamage == castedOther.maxDamage &&
                    this.fireResistant == castedOther.fireResistant &&
                    Objects.equals(this.rarity, castedOther.rarity) &&
                    Objects.equals(this.recipeRemainder, castedOther.recipeRemainder) &&
                    foodPropertyEquals;
        }

        public ItemEditor applyToItemEditor() {
            ItemEditor editor = ItemEditor.getInstance(this.targetItem);
            editor.setMaxStackSize(this.maxStackSize);
            editor.setMaxDamage(this.maxDamage);
            editor.setFireResistance(this.fireResistant);
            editor.setRarity(this.rarity);
            editor.setRecipeRemainder(this.recipeRemainder);
            editor.setFoodComponent(this.foodProperty);
            return editor;
        }

        public ItemEditor revertItemEditor() {
            ItemEditor editor = ItemEditor.getInstance(this.targetItem);
            editor.revertVanilla();
            return editor;
        }
    }

    public static class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
        @Override
        public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Identifier itemId = context.deserialize(json, Identifier.class);
            return ConfigLoader.getAllItems().get(itemId);
        }

        @Override
        public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
            Identifier srcId = ConfigLoader.getAllItems().inverse().get(src);
            return context.serialize(srcId);
        }
    }

    public static class FoodComponentTypeAdapter implements JsonSerializer<FoodComponent>, JsonDeserializer<FoodComponent> {
        @Override
        public FoodComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int nutrition = jsonObject.get("nutrition").getAsInt();
            float saturation = jsonObject.get("saturation").getAsFloat();
            boolean canAlwaysEat = jsonObject.get("canAlwaysEat").getAsBoolean();
            float eatSeconds = jsonObject.get("eatSeconds").getAsFloat();
            JsonElement leftOverStackJson = jsonObject.get("leftOverStack");
            Optional<ItemStack> leftOverStackOptional;
            if (leftOverStackJson == null) {
                leftOverStackOptional = Optional.empty();
            } else {
                leftOverStackOptional = Optional.of(context.deserialize(leftOverStackJson, ItemStack.class));
            }
            List<JsonElement> effectsJsonList = jsonObject.get("effects").getAsJsonArray().asList();
            List<FoodComponent.StatusEffectEntry> effectsList = new ArrayList<>();
            for (JsonElement effectJson : effectsJsonList) {
                effectsList.add(context.deserialize(effectJson, FoodComponent.StatusEffectEntry.class));
            }
            return new FoodComponent(nutrition, saturation, canAlwaysEat, eatSeconds, leftOverStackOptional, effectsList);
        }

        @Override
        public JsonElement serialize(FoodComponent src, Type typeOfSrc, JsonSerializationContext context) {
            int nutrition = src.nutrition();
            float saturation = src.saturation();
            boolean canAlwaysEat = src.canAlwaysEat();
            float eatSeconds = src.eatSeconds();
            ItemStack leftOverStack = src.usingConvertsTo().orElse(null);
            List<FoodComponent.StatusEffectEntry> effects = src.effects();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("nutrition", new JsonPrimitive(nutrition));
            jsonObject.add("saturation", new JsonPrimitive(saturation));
            jsonObject.add("canAlwaysEat", new JsonPrimitive(canAlwaysEat));
            jsonObject.add("eatSeconds", new JsonPrimitive(eatSeconds));
            jsonObject.add("leftOverStack", context.serialize(leftOverStack));
            jsonObject.add("effects", context.serialize(effects));
            return jsonObject;
        }
    }

    public static class FoodItemStackTypeAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Item carriedItem = context.deserialize(json, Item.class);
            return new ItemStack(carriedItem);
        }

        @Override
        public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
            Item containedItem = src.getItem();
            return context.serialize(containedItem);
        }
    }

    public static class FoodStatusEffectEntryTypeAdapter implements JsonSerializer<FoodComponent.StatusEffectEntry>, JsonDeserializer<FoodComponent.StatusEffectEntry> {

        @Override
        public FoodComponent.StatusEffectEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            StatusEffectInstance effect = context.deserialize(jsonObject.get("effect"), StatusEffectInstance.class);
            float probability = jsonObject.get("probability").getAsFloat();
            return new FoodComponent.StatusEffectEntry(effect, probability);
        }

        @Override
        public JsonElement serialize(FoodComponent.StatusEffectEntry src, Type typeOfSrc, JsonSerializationContext context) {
            StatusEffectInstance effect = src.effect();
            float probability = src.probability();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("effect", context.serialize(effect));
            jsonObject.add("probability", new JsonPrimitive(probability));
            return jsonObject;
        }
    }

    public static class FoodStatusEffectInstanceTypeAdapter implements JsonSerializer<StatusEffectInstance>, JsonDeserializer<StatusEffectInstance> {
        @Override
        public StatusEffectInstance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Identifier effectId = context.deserialize(jsonObject.get("effect"), Identifier.class);
            RegistryEntry<StatusEffect> effect = ConfigLoader.getAllStatusEffects().get(effectId);
            int amplifier = jsonObject.get("amplifier").getAsInt();
            int duration = jsonObject.get("duration").getAsInt();
            return new StatusEffectInstance(effect, duration, amplifier);
        }

        @Override
        public JsonElement serialize(StatusEffectInstance src, Type typeOfSrc, JsonSerializationContext context) {
            RegistryEntry<StatusEffect> effect = src.getEffectType();
            Identifier effectId = ConfigLoader.getAllStatusEffects().inverse().get(effect);
            int amplifier = src.getAmplifier();
            int duration = src.getDuration();
            JsonObject json = new JsonObject();
            json.add("effect", context.serialize(effectId));
            json.add("amplifier", new JsonPrimitive(amplifier));
            json.add("duration", new JsonPrimitive(duration));
            return json;
        }
    }

    public static class IdentifierTypeAdapter implements JsonSerializer<Identifier>, CustomJsonDeserializer<Identifier> {

        @Override
        public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return parseIdentifier(json);
        }

        @Override
        public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
            String literal = src.getNamespace() + ":" + src.getPath();
            return new JsonPrimitive(literal);
        }
    }
}