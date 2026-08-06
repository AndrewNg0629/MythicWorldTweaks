package top.aenp.mwt.injected.interfaces;

import net.minecraft.block.entity.VaultBlockEntity;
import org.apache.commons.lang3.NotImplementedException;

public interface VaultServerDataMethodInjections {
    default void mythicworldtweaks$storeVaultBlockEntity(VaultBlockEntity entity) {
        throw new NotImplementedException();
    }
}
