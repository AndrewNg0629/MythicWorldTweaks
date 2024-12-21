package online.andrew2007.itemCenter;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import online.andrew2007.MythicWorldTweaks;
import online.andrew2007.util.MythicWorldTweaksToggle;
import online.andrew2007.util.ReflectionCenter;

public class ItemInitializer {
    public static final Item LARGE_FIRE_CHARGE = registerItem("large_fire_charge", new LargeFireChargeItem(new Item.Settings()));
    public static final Item DEBUGGER = registerItem("debugger", new DebuggerItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(DebuggerItem.DEBUG_SELECTION, 1)));
    //public static final ArrayList<ItemPropertiesEditor> itemModifications = new ArrayList<>();
    public static final Item[] shulkerBoxes = {
            Items.SHULKER_BOX,
            Items.WHITE_SHULKER_BOX,
            Items.ORANGE_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX,
            Items.LIGHT_BLUE_SHULKER_BOX,
            Items.YELLOW_SHULKER_BOX,
            Items.LIME_SHULKER_BOX,
            Items.PINK_SHULKER_BOX,
            Items.GRAY_SHULKER_BOX,
            Items.LIGHT_GRAY_SHULKER_BOX,
            Items.CYAN_SHULKER_BOX,
            Items.PURPLE_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX,
            Items.BROWN_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX,
            Items.RED_SHULKER_BOX,
            Items.BLACK_SHULKER_BOX
    };
    public static final Item[] smithingTemplates = {
            Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
            Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,
            Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE
    };

    public static void generalInitialization() {
        largeFireChargeInit();
        debuggerInit();
        dispensableTrident();
        itemModificationsInit();
    }

    public static void itemModificationsInit() {
        /*
        itemModifications.add(new ItemPropertiesEditor(Items.POTION, ItemPropertiesEditor.Property.MAX_COUNT, 32));
        itemModifications.add(new ItemPropertiesEditor(Items.SPLASH_POTION, ItemPropertiesEditor.Property.MAX_COUNT, 32));
        itemModifications.add(new ItemPropertiesEditor(Items.LINGERING_POTION, ItemPropertiesEditor.Property.MAX_COUNT, 32));
        itemModifications.add(new ItemPropertiesEditor(Items.SNOWBALL, ItemPropertiesEditor.Property.MAX_COUNT, 64));
        itemModifications.add(new ItemPropertiesEditor(Items.ENDER_PEARL, ItemPropertiesEditor.Property.MAX_COUNT, 32));
        itemModifications.add(new ItemPropertiesEditor(Items.HONEY_BOTTLE, ItemPropertiesEditor.Property.MAX_COUNT, 64));
        itemModifications.add(new ItemPropertiesEditor(Items.MILK_BUCKET, ItemPropertiesEditor.Property.MAX_COUNT, 64));
        itemModifications.add(new ItemPropertiesEditor(Items.EGG, ItemPropertiesEditor.Property.MAX_COUNT, 64));
        itemModifications.add(new ItemPropertiesEditor(Items.CAKE, ItemPropertiesEditor.Property.MAX_COUNT, 64));
        for (Item shulkerBox : shulkerBoxes) {
            itemModifications.add(new ItemPropertiesEditor(shulkerBox, ItemPropertiesEditor.Property.MAX_COUNT, 64));
        }
        itemModifications.add(new ItemPropertiesEditor(Items.ELYTRA, ItemPropertiesEditor.Property.MAX_DAMAGE, 900));
        for (Item smithingTemplate : smithingTemplates) {
            itemModifications.add(new ItemPropertiesEditor(smithingTemplate, ItemPropertiesEditor.Property.RECIPE_REMAINDER, smithingTemplate));
        }*/
    }

    public static void applyItemModifications() {
        //for (ItemPropertiesEditor itemModification : itemModifications) {
        //itemModification.applyEdit();
        //}
    }

    public static void revertItemModifications() {
        //for (ItemPropertiesEditor itemModification : itemModifications) {
        //itemModification.revertEdit();
        //}
    }

    public static Item registerItem(String itemKey, Item item) {

        return Registry.register(Registries.ITEM, Identifier.of(MythicWorldTweaks.MOD_ID, itemKey), item);
    }

    public static void debuggerInit() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register((itemGroup) -> itemGroup.add(DEBUGGER));
    }

    public static void largeFireChargeInit() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> itemGroup.addAfter(Items.FIRE_CHARGE, LARGE_FIRE_CHARGE));
        DispenserBlock.registerBehavior(LARGE_FIRE_CHARGE, new ItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if (MythicWorldTweaksToggle.LARGE_FIRE_CHARGE.isEnabled()) {
                    Direction direction = pointer.state().get(DispenserBlock.FACING);
                    Position position = DispenserBlock.getOutputLocation(pointer);
                    World world = pointer.world();
                    double initPosX = position.getX() + (double) ((float) direction.getOffsetX() * 0.3F);
                    double initPosY = position.getY() + (double) ((float) direction.getOffsetY() * 0.3F);
                    double initPosZ = position.getZ() + (double) ((float) direction.getOffsetZ() * 0.3F);
                    FireballEntity fireballEntity = new FireballEntity(EntityType.FIREBALL, world);
                    fireballEntity.setPosition(initPosX, initPosY, initPosZ);
                    fireballEntity.setVelocity(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ(), 1.0F, 1.0F);
                    ReflectionCenter.setFieldValue(ReflectionCenter.explosionPower, fireballEntity, 1);
                    world.spawnEntity(fireballEntity);
                    stack.decrement(1);
                    return stack;
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
            }

            @Override
            protected void playSound(BlockPointer pointer) {
                if (MythicWorldTweaksToggle.LARGE_FIRE_CHARGE.isEnabled()) {
                    pointer.world().playSound(
                            null,
                            pointer.pos().getX(),
                            pointer.pos().getY(),
                            pointer.pos().getZ(),
                            SoundEvents.ENTITY_GHAST_SHOOT,
                            SoundCategory.NEUTRAL,
                            1.0F,
                            1.0F);
                } else {
                    super.playSound(pointer);
                }
            }
        });
    }

    public static void dispensableTrident() {
        DispenserBlock.registerBehavior(Items.TRIDENT, new FallibleItemDispenserBehavior() {
            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                if (MythicWorldTweaksToggle.DISPENSABLE_TRIDENT.isEnabled()) {
                    this.setSuccess(false);
                    if (stack.getMaxDamage() - stack.getDamage() <= 1) {
                        pointer.world().playSound(
                                null,
                                pointer.pos().getX(),
                                pointer.pos().getY(),
                                pointer.pos().getZ(),
                                SoundEvents.BLOCK_DISPENSER_FAIL,
                                SoundCategory.NEUTRAL,
                                1.0F,
                                1.0F);
                        return stack;
                    }
                    stack.damage(1, pointer.world(), null, item -> {
                    });
                    Direction direction = pointer.state().get(DispenserBlock.FACING);
                    Position position = DispenserBlock.getOutputLocation(pointer);
                    World world = pointer.world();
                    double initPosX = position.getX() + (double) ((float) direction.getOffsetX() * 0.3F);
                    double initPosY = position.getY() + (double) ((float) direction.getOffsetY() * 0.3F);
                    double initPosZ = position.getZ() + (double) ((float) direction.getOffsetZ() * 0.3F);
                    TridentEntity tridentEntity = new TridentEntity(EntityType.TRIDENT, world);
                    ReflectionCenter.setFieldValue(ReflectionCenter.stack, tridentEntity, stack.copy());
                    tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    //noinspection unchecked
                    tridentEntity.getDataTracker().set(ReflectionCenter.getFieldValue(ReflectionCenter.ENCHANTED, null), stack.hasGlint());
                    tridentEntity.setPos(initPosX, initPosY, initPosZ);
                    tridentEntity.setVelocity(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ(), 2.5F, 1.0F);
                    world.spawnEntity(tridentEntity);
                    pointer.world().playSound(
                            null,
                            pointer.pos().getX(),
                            pointer.pos().getY(),
                            pointer.pos().getZ(),
                            SoundEvents.ITEM_TRIDENT_THROW,
                            SoundCategory.NEUTRAL,
                            1.0F,
                            1.0F);
                    stack.decrement(1);
                    this.setSuccess(true);
                    return stack;
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
            }

            @Override
            protected void playSound(BlockPointer pointer) {
                if (!MythicWorldTweaksToggle.DISPENSABLE_TRIDENT.isEnabled()) {
                    super.playSound(pointer);
                }
            }
        });
    }
}
