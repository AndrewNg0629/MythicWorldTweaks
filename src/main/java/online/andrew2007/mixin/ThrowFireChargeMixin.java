package online.andrew2007.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import online.andrew2007.util.MythicWorldTweaksToggle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ThrowFireChargeMixin {
    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        if ((Item) (Object) this instanceof FireChargeItem thisOBJ && MythicWorldTweaksToggle.THROWABLE_FIRE_CHARGE.isEnabled()) {
            ItemStack itemStack = user.getStackInHand(hand);
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ENTITY_BLAZE_SHOOT,
                    SoundCategory.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (!world.isClient) {
                SmallFireballEntity smallFireballEntity = new SmallFireballEntity(EntityType.SMALL_FIREBALL, world);
                smallFireballEntity.setPosition(user.getX(), user.getEyeY() - 0.1F, user.getZ());
                smallFireballEntity.setOwner(user);
                smallFireballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
                world.spawnEntity(smallFireballEntity);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(thisOBJ));
            if (!user.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            info.setReturnValue(TypedActionResult.success(itemStack, world.isClient()));
        }
    }
}