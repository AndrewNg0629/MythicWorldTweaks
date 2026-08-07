package top.aenp.mwt.mixin;

import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.aenp.mwt.injected.interfaces.VaultBlockEntityMethodInjections;
import top.aenp.mwt.misc.VaultStuff;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(VaultBlockEntity.class)
public class VaultBlockEntityMixin implements VaultBlockEntityMethodInjections {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    private final ConcurrentHashMap<UUID, Integer> vaultCooldown = new ConcurrentHashMap<>();
    @Shadow
    @Final
    private VaultServerData serverData;

    @Override
    public ConcurrentHashMap<UUID, Integer> mythicworldtweaks$getCooldownMap() {
        return vaultCooldown;
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At(value = "RETURN"))
    private void init(CallbackInfo info) {
        serverData.mythicworldtweaks$storeVaultBlockEntity((VaultBlockEntity) (Object) this);
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (nbt.contains("mwt_vault_cooldown")) {
            VaultStuff.COOLDOWN_MAP_CODEC.parse(NbtOps.INSTANCE, nbt.get("mwt_vault_cooldown")).resultOrPartial(LOGGER::error).ifPresent(map -> {
                vaultCooldown.clear();
                vaultCooldown.putAll(map);
            });
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void writeNnt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        nbt.put("mwt_vault_cooldown", VaultStuff.COOLDOWN_MAP_CODEC.encodeStart(NbtOps.INSTANCE, vaultCooldown).getOrThrow());
    }
}
