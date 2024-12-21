package online.andrew2007.mixin;

import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DataComponentTypes.class)
public class DataComponentTypesMixin {
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/Codecs;rangedInt(II)Lcom/mojang/serialization/Codec;"),
            method = "method_58570(Lnet/minecraft/component/ComponentType$Builder;)Lnet/minecraft/component/ComponentType$Builder;",
            index = 1)
    private static int itemMaxCountRangeExpansion(int count) {
        return Integer.MAX_VALUE;
    }
}