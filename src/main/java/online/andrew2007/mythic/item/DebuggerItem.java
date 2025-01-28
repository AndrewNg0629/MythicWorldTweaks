package online.andrew2007.mythic.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
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
import online.andrew2007.mythic.util.MythicWorldTweaksToggle;
import online.andrew2007.mythic.util.PlayerEntityUtil;

public class DebuggerItem extends Item {
    public static final ComponentType<Integer> DEBUG_SELECTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MythicWorldTweaks.MOD_ID, "debug_selection"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    public final int debugSelectionsCount = 21;

    public DebuggerItem(Settings settings) {
        super(settings);
    }

    public void debugAction(int selection, PlayerEntity user) {
        if (selection == 20) {
            user.sendMessage(Text.of(user.getDataTracker().get(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION).toString()));
            user.getDataTracker().set(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION, true);
            user.sendMessage(Text.of(user.getDataTracker().get(PlayerEntityUtil.IS_UNDER_FALL_PROTECTION).toString()));
            return;
        } else if (selection == 21) {
            user.sendMessage(Text.of(user.getPose().toString() + user.isFallFlying()));
        }
        int ordinal = selection - 1;
        MythicWorldTweaksToggle feature = MythicWorldTweaksToggle.values()[ordinal];
        boolean featureEnabled = feature.isEnabled();
        String featureName = feature.name();
        feature.setEnabled(!featureEnabled);
        user.sendMessage(Text.of(String.format("Toggled \"%s\" %s.", featureName, !featureEnabled ? "on" : "off")));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.contains(DEBUG_SELECTION) && !user.getWorld().isClient()) {
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
                debugAction(debugSelection, user);
            }
            return TypedActionResult.success(stack, world.isClient());
        } else {
            return super.use(world, user, hand);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 60;
    }
}

