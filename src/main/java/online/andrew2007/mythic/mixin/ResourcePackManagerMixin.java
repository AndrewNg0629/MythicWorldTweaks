package online.andrew2007.mythic.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Unique
    private static boolean dtpStatusPrinted = false;

    @Inject(at = @At(value = "RETURN"), method = "providePackProfiles", cancellable = true)
    private void providePackProfiles(CallbackInfoReturnable<Map<String, ResourcePackProfile>> info, @Local Map<String, ResourcePackProfile> map) {
        if (!dtpStatusPrinted) {
            MythicWorldTweaks.LOGGER.info("MythicWorldTweaks mod data pack is {}.", RuntimeController.getLocalRuntimeParams().modDataPackEnabled() ? "enabled" : "disabled");
            dtpStatusPrinted = true;
        }
        if (!RuntimeController.getLocalRuntimeParams().modDataPackEnabled()) {
            Map<String, ResourcePackProfile> copyMap = new HashMap<>(map);
            copyMap.remove(MythicWorldTweaks.DATA_MOD_ID);
            info.setReturnValue(copyMap);
        }
    }
}
