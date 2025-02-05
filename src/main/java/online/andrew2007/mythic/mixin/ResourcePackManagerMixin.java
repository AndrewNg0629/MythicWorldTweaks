package online.andrew2007.mythic.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import online.andrew2007.mythic.MythicWorldTweaks;
import online.andrew2007.mythic.config.RuntimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

import static online.andrew2007.mythic.MythicWorldTweaks.MOD_ID;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Inject(at = @At(value = "RETURN"), method = "providePackProfiles", cancellable = true)
    private void providePackProfiles(CallbackInfoReturnable<Map<String, ResourcePackProfile>> info, @Local Map<String, ResourcePackProfile> map) {
        if (!RuntimeController.getLocalRuntimeParams().modDataPackEnabled() && false) {
            Map<String, ResourcePackProfile> copyMap = new HashMap<>(map);
            copyMap.remove(MOD_ID);
            info.setReturnValue(copyMap);
        }
    }
}
