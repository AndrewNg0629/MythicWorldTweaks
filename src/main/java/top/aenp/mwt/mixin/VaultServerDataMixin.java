package top.aenp.mwt.mixin;

import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;
import top.aenp.mwt.interfaces.MwtVaultServerData;

import java.util.Set;
import java.util.UUID;

@Mixin(VaultServerData.class)
public abstract class VaultServerDataMixin implements MwtVaultServerData {
    @Unique
    private VaultBlockEntity vaultBlockEntity;

    @Shadow
    protected abstract void markDirty();

    @Override
    public void mythicworldtweaks$storeVaultBlockEntity(VaultBlockEntity entity) {
        vaultBlockEntity = entity;
    }

    @Unique
    private void executeIfReused(Runnable runnable) {
        ModConfig.Tweaks.ValueTweaks.VaultReuse vaultReuseConfig = ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse();
        if (vaultBlockEntity.getCachedState().get(VaultBlock.OMINOUS) ? vaultReuseConfig.reuseOminousVault() : vaultReuseConfig.reuseRegularVault()) {
            runnable.run();
        }
    }

    @Inject(method = "getRewardedPlayers", at = @At(value = "RETURN"), cancellable = true)
    private void getRewardedPlayers(CallbackInfoReturnable<Set<UUID>> info) {
        executeIfReused(() -> info.setReturnValue(vaultBlockEntity.mythicworldtweaks$getTimestampMap().keySet()));
    }

    @Inject(method = "hasRewardedPlayer", at = @At(value = "RETURN"), cancellable = true)
    private void hasRewardedPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        executeIfReused(() -> info.setReturnValue(vaultBlockEntity.mythicworldtweaks$getTimestampMap().containsKey(player.getUuid())));
    }

    @Inject(method = "markPlayerAsRewarded", at = @At(value = "HEAD"), cancellable = true)
    private void markPlayerAsRewarded(PlayerEntity player, CallbackInfo info) {
        executeIfReused(() -> {
            World world = vaultBlockEntity.getWorld();
            if (world != null) {
                vaultBlockEntity.mythicworldtweaks$getTimestampMap().put(player.getUuid(), world.getTime());
                markDirty();
                info.cancel();
            }
        });
    }
}
