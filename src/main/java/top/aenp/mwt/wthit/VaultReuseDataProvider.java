package top.aenp.mwt.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VaultReuseDataProvider implements IDataProvider<VaultBlockEntity> {
    public static final IData.Type<Data> TYPE = IData.createType(Identifier.of(MythicWorldTweaks.MOD_ID, "vault_reuse"));
    public static final PacketCodec<PacketByteBuf, Data> CODEC = new PacketCodec<>() {
        @Override
        public Data decode(PacketByteBuf buf) {
            return new Data(buf.readInt());
        }

        @Override
        public void encode(PacketByteBuf buf, Data value) {
            buf.writeInt(value.remainingTicks);
        }
    };

    @Override
    public void appendData(IDataWriter data, IServerAccessor<VaultBlockEntity> accessor, IPluginConfig config) {
        if (ConfigManager.getConfig().multiplayerSupportEnabled()) {
            ModConfig.Tweaks.ValueTweaks.VaultReuse vaultReuseConfig = ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse();
            boolean ominous = accessor.getTarget().getCachedState().get(VaultBlock.OMINOUS);
            if (ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse().wthitIntegration() && (ominous ? vaultReuseConfig.reuseOminousVault() : vaultReuseConfig.reuseRegularVault())) {
                ConcurrentHashMap<UUID, Integer> cooldownMap = accessor.getTarget().mythicworldtweaks$getCooldownMap();
                UUID uuid = accessor.getPlayer().getUuid();
                int targetCooldownTicks = (ominous ? vaultReuseConfig.ominousVaultCooldown() : vaultReuseConfig.regularVaultCooldown()) * 20;
                if (cooldownMap.containsKey(uuid)) {
                    data.add(TYPE, result -> result.add(new Data(targetCooldownTicks - cooldownMap.get(uuid))));
                } else {
                    data.add(TYPE, result -> result.add(new Data(-1)));
                }
            }
        }
    }

    public record Data(int remainingTicks) implements IData {
        @Override
        public Type<? extends IData> type() {
            return TYPE;
        }
    }
}
