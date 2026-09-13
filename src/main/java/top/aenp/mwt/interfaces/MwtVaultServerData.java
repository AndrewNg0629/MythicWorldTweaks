package top.aenp.mwt.interfaces;

import net.minecraft.block.entity.VaultBlockEntity;

public interface MwtVaultServerData {
    default void mythicworldtweaks$storeVaultBlockEntity(VaultBlockEntity entity) {
        throw new RuntimeException();
    }
}
