package top.aenp.mwt.wthit.client;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.block.VaultBlock;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.wthit.VaultReuseDataProvider;

public enum VaultReuseProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse().wthitIntegration() && ConfigManager.getConfig().multiplayerSupportEnabled()) {
            VaultReuseDataProvider.Data data = accessor.getData().get(VaultReuseDataProvider.TYPE);
            if (data != null) {
                int remainingTicks = (accessor.getBlockState().get(VaultBlock.OMINOUS) ? ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse().ominousVaultCooldown() : ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse().regularVaultCooldown()) * 20 - data.elapsedTicks();
                if (remainingTicks >= 0) {
                    int totalSeconds = remainingTicks / 20;
                    int hours = totalSeconds / 3600;
                    int minutes = (totalSeconds % 3600) / 60;
                    int seconds = totalSeconds % 60;
                    String time = hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
                    tooltip.addLine(Text.translatable("mythicworldtweaks.wthit.tooltip.vault.cooldown", time).formatted(Formatting.YELLOW));
                } else {
                    tooltip.addLine(Text.translatable("mythicworldtweaks.wthit.tooltip.vault.ready").formatted(Formatting.GREEN));
                }
            }
        }
    }
}
