package online.andrew2007.config;


import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import online.andrew2007.config.configPatterns.ItemEditorConfig;
import online.andrew2007.util.MiscUtil;

public class ItemEditorIntermediaryConfig {
    public final Identifier itemIdentifier;
    public final boolean itemDamageable;
    private final Pair<Boolean, Integer> maxCountOverride = new Pair<>(false, null);
    private final Pair<Boolean, Integer> maxDamageOverride = new Pair<>(false, null);
    private final Pair<Boolean, String> rarityOverride = new Pair<>(false, null);
    private final Pair<Boolean, Boolean> fireResistanceOverride = new Pair<>(false, null);
    private final Pair<Boolean, String> recipeRemainderOverride = new Pair<>(false, null);

    public ItemEditorIntermediaryConfig(String rawName, ItemEditorConfig.ItemProperties rawProperties) throws IllegalArgumentException {
        String[] parts = rawName.trim().split(":");
        String nameSpace;
        String path;
        if (parts.length == 2) {
            nameSpace = parts[0];
            path = parts[1];
            if (!Identifier.isNamespaceValid(nameSpace) || !Identifier.isPathValid(path)) {
                throw new IllegalArgumentException(String.format("Invalid item name: %s", rawName));
            }
        } else if (parts.length == 1) {
            nameSpace = "minecraft";
            path = parts[0];
            if (!Identifier.isPathValid(path)) {
                throw new IllegalArgumentException(String.format("Invalid item name: %s", rawName));
            }
        } else {
            throw new IllegalArgumentException(String.format("Invalid item name: %s", rawName));
        }
        this.itemIdentifier = Identifier.of(nameSpace, path);
        Item item = MiscUtil.items.get(this.itemIdentifier);
        if (item == null) {
            throw new IllegalArgumentException(String.format("Invalid item name: %s", rawName));
        }
        this.itemDamageable = item.getComponents().contains(DataComponentTypes.MAX_DAMAGE);
        if (this.itemDamageable) {
            if (rawProperties.maxCount != null) {
                if (rawProperties.maxCount > 1) {
                    throw new IllegalArgumentException("As an old saying goes, items cannot be both stackable and damageable.");
                }
            }
            if (rawProperties.maxDamage != null) {
                if (rawProperties.maxCount > 1) {
                    throw new IllegalArgumentException("As an old saying goes, item cannot be both stackable and damageable.");
                }
            }

        }
    }
}
