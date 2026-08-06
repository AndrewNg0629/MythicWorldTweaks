package top.aenp.mwt.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mixin(VaultBlock.class)
public class VaultBlockMixin {
    @Inject(method = "method_56718", at = @At(value = "RETURN"))
    private static void serverTick(ServerWorld serverWorld, World world, BlockPos pos, BlockState state, VaultBlockEntity blockEntity, CallbackInfo info) {
        ModConfig.Tweaks.ValueTweaks.VaultReuse vaultReuseConfig = ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse();
        boolean ominous = state.get(VaultBlock.OMINOUS);
        if (ominous ? vaultReuseConfig.reuseOminousVault() : vaultReuseConfig.reuseRegularVault()) {
            int cooldownTicks = ominous ? vaultReuseConfig.ominousVaultCooldown() * 20 : vaultReuseConfig.regularVaultCooldown() * 20;
            Iterator<Map.Entry<UUID, Integer>> iterator = blockEntity.mythicworldtweaks$getCooldownMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                int ticksElapsed = entry.getValue() + 1;
                if (ticksElapsed >= cooldownTicks) {
                    iterator.remove();
                } else {
                    entry.setValue(ticksElapsed);
                }
            }
        }
    }
}
