package top.aenp.mwt.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.aenp.mwt.config.v2.ConfigManager;

import java.util.ArrayList;
import java.util.List;

@Mixin(SleepManager.class)
public class SleepManagerMixin {
    @ModifyVariable(at = @At(value = "HEAD"), method = "update", ordinal = 0, argsOnly = true)
    private List<ServerPlayerEntity> removeFakePlayers(List<ServerPlayerEntity> players) {
        if (ConfigManager.getConfig().tweaks().localToggleTweaks1().carpetFakePlayerSleepExclusion()) {
            ArrayList<ServerPlayerEntity> list = new ArrayList<>(players);
            list.removeIf(ServerPlayerEntity::mythicWorldTweaks$isFake);
            return list;
        } else {
            return players;
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSleeping()Z"), method = "update")
    private boolean isReallySleeping(ServerPlayerEntity instance) {
        if (ConfigManager.getConfig().tweaks().syncedToggleTweaks1().bedIdle()) {
            return instance.isSleeping() && instance.mythicWorldTweaks$isReallySleeping();
        } else {
            return instance.isSleeping();
        }
    }
}
