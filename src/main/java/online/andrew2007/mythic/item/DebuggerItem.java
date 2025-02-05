package online.andrew2007.mythic.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.util.PlayerEntityUtil;

public class DebuggerItem extends Item {
    public static final ComponentType<Integer> DEBUG_SELECTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MythicWorldTweaks.MOD_ID, "debug_selection"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    public final int debugSelectionsCount = 3;

    public DebuggerItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.contains(DEBUG_SELECTION)) {
            int debugSelection = stack.getOrDefault(DEBUG_SELECTION, 1);
            if (user.isSneaking()) {
                if (debugSelection >= debugSelectionsCount) {
                    debugSelection = 1;
                } else {
                    debugSelection++;
                }
                stack.set(DEBUG_SELECTION, debugSelection);
                user.sendMessage(Text.of(String.format("Debug selection: %s", debugSelection)));
            } else {
                debugAction(debugSelection, world, user);
            }
            return TypedActionResult.success(stack, world.isClient());
        } else {
            return super.use(world, user, hand);
        }
    }

    private void debugAction(int debugSelection, World world, PlayerEntity user) {
        switch (debugSelection) {
            case 1:
                if (world.isClient) {
                    boolean bl = user.getDataTracker().get(PlayerEntityUtil.IS_REALLY_SLEEPING);
                    user.getDataTracker().set(PlayerEntityUtil.IS_REALLY_SLEEPING, !bl);
                    MythicWorldTweaks.LOGGER.info("Client: Debug data set to {}.", !bl);
                }
                break;
            case 2:
                if (!world.isClient) {
                    boolean bl = user.getDataTracker().get(PlayerEntityUtil.IS_REALLY_SLEEPING);
                    user.getDataTracker().set(PlayerEntityUtil.IS_REALLY_SLEEPING, !bl);
                    MythicWorldTweaks.LOGGER.info("Server: Debug data set to {}.", !bl);
                }
                break;
            case 3:
                boolean bl = user.getDataTracker().get(PlayerEntityUtil.IS_REALLY_SLEEPING);
                if (world.isClient) {
                    MythicWorldTweaks.LOGGER.info("Client: Debug data is {}.", bl);
                } else {
                    MythicWorldTweaks.LOGGER.info("Server: Debug data is {}.", bl);
                }
                break;
            default:
                break;
        }
    }
}
