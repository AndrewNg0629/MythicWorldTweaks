package top.aenp.mwt.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.command.GiveCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.aenp.mwt.config.v2.ConfigManager;

@Mixin(GiveCommand.class)
public class GiveCommandMixin {
    @ModifyVariable(at = @At(value = "STORE"), ordinal = 2, method = "execute")
    private static int modifyMaxGiveCount(int value, @Local(ordinal = 1) int maxCount) {
        if (ConfigManager.getConfig().itemEditorConfig().enabled() && maxCount > 21474836) {
            return Integer.MAX_VALUE;
        } else {
            return value;
        }
    }
}
