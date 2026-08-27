package top.aenp.mwt.wthit.client;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.aenp.mwt.misc.TrialChamberStuff;
import top.aenp.mwt.wthit.VaultReuseDataProvider;

public class VaultReuseProvider implements IBlockComponentProvider {
    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        VaultReuseDataProvider.Data data = accessor.getData().get(VaultReuseDataProvider.TYPE);
        if (data != null) {
            int remainingTicks = data.remainingTicks();
            if (remainingTicks >= 0) {
                tooltip.addLine(TrialChamberStuff.formatCooldown(remainingTicks));
            } else {
                tooltip.addLine(Text.translatable("mythicworldtweaks.wthit.tooltip.vault.ready").formatted(Formatting.GREEN));
            }
        }
    }
}
