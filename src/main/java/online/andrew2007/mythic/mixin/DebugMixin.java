package online.andrew2007.mythic.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class DebugMixin {
    @Inject(at = @At(value = "RETURN"), method = "createPlayer")
    private void createPlayer(GameProfile profile, SyncedClientOptions syncedOptions, CallbackInfoReturnable<ServerPlayerEntity> info) {
        if (RuntimeController.getCurrentTParams().autoDiscardingFireBallEnabled()) {
            MythicWorldTweaks.LOGGER.warn("Delay starts.");
            try {
                Thread.sleep(RuntimeController.getCurrentTParams().fireBallMaxLifeTicks());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
