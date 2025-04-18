package online.andrew2007.mythic.item;

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
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import online.andrew2007.mythic.modFunctions.ReflectionCenter;

@SuppressWarnings("resource") //We can't close the ServerWorld after playing sounds.
public class ItemInitializer {
    public static final Item LARGE_FIRE_CHARGE = registerItem("large_fire_charge", new LargeFireChargeItem(new Item.Settings()));
    public static final Item DEBUGGER = registerItem("debugger", new DebuggerItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));

    public static void generalInitialization() {
        largeFireChargeInit();
        debuggerInit();
        dispensableTrident();
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
                if (RuntimeController.getCurrentTParams().largeFireCharge()) {
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
                if (RuntimeController.getCurrentTParams().largeFireCharge()) {
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
                if (RuntimeController.getCurrentTParams().dispensableTridents()) {
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
                if (!RuntimeController.getCurrentTParams().dispensableTridents()) {
                    super.playSound(pointer);
                }
            }
        });
    }
}
