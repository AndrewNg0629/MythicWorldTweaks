package top.aenp.mwt.wthit.client;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;

@SuppressWarnings("unused")
public class MythicWailaClientPlugin implements IWailaClientPlugin {
    @Override
    public void register(IClientRegistrar registrar) {
        registrar.body(new VaultReuseProvider(), VaultBlockEntity.class);
        registrar.body(new TrialSpawnerCooldownProvider(), TrialSpawnerBlockEntity.class);
    }
}
