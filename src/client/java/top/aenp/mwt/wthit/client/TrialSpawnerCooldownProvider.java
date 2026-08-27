package top.aenp.mwt.wthit.client;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import top.aenp.mwt.misc.TrialChamberStuff;
import top.aenp.mwt.wthit.TrialSpawnerCooldownDataProvider;

public class TrialSpawnerCooldownProvider implements IBlockComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        TrialSpawnerCooldownDataProvider.Data data = accessor.getData().get(TrialSpawnerCooldownDataProvider.TYPE);
        if (data != null) {
            int remainingTicks = data.remainingTicks();
            if (remainingTicks >= 0) {
                tooltip.addLine(TrialChamberStuff.formatCooldown(remainingTicks));
            }
        }
    }
}
