package top.aenp.mwt.wthit;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import net.minecraft.block.entity.VaultBlockEntity;

@SuppressWarnings("unused")
public class MythicWailaCommonPlugin implements IWailaCommonPlugin {
    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.dataType(VaultReuseDataProvider.TYPE, VaultReuseDataProvider.CODEC);
        registrar.blockData(VaultReuseDataProvider.INSTANCE, VaultBlockEntity.class);
    }
}
