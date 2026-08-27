package top.aenp.mwt.mixin;

import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawnerBlockEntity.class)
public class TrialSpawnerBlockEntityMixin {
    @Shadow
    private TrialSpawnerLogic spawner;

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        if (nbt.contains("mwt_completion_elapsed_ticks")) {
            spawner.getData().mythicworldtweaks$setCompletionElapsedTicks(nbt.getInt("mwt_completion_elapsed_ticks"));
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        nbt.putInt("mwt_completion_elapsed_ticks", spawner.getData().mythicworldtweaks$getCompletionElapsedTicks());
    }
}
