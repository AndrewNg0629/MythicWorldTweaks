package top.aenp.mwt.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.config.v2.ConfigManager;

public enum VaultReuseDataProvider implements IDataProvider<VaultBlockEntity> {
    INSTANCE;
    public static final IData.Type<Data> TYPE = IData.createType(Identifier.of("mythicworldtweaks", "vault_reuse"));
    public static final PacketCodec<PacketByteBuf, Data> CODEC = new PacketCodec<>() {
        @Override
        public Data decode(PacketByteBuf buf) {
            return new Data(buf.readInt());
        }

        @Override
        public void encode(PacketByteBuf buf, Data value) {
            buf.writeInt(value.elapsedTicks);
        }
    };

    @Override
    public void appendData(IDataWriter data, IServerAccessor<VaultBlockEntity> accessor, IPluginConfig config) {
        if (ConfigManager.getConfig().tweaks().valueTweaks().vaultReuse().wthitIntegration()) {
            data.add(TYPE, result -> {
                int elapsedTicks = accessor.getTarget().mythicworldtweaks$getCooldownMap().getOrDefault(accessor.getPlayer().getUuid(), Integer.MAX_VALUE);
                result.add(new Data(elapsedTicks));
            });

        }
    }

    public record Data(int elapsedTicks) implements IData {
        @Override
        public Type<? extends IData> type() {
            return TYPE;
        }
    }
}
