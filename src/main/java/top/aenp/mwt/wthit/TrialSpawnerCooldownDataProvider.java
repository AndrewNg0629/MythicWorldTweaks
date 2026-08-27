package top.aenp.mwt.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import top.aenp.mwt.MythicWorldTweaks;
import top.aenp.mwt.config.v2.ConfigManager;
import top.aenp.mwt.config.v2.ModConfig;

public class TrialSpawnerCooldownDataProvider implements IDataProvider<TrialSpawnerBlockEntity> {
    public static final IData.Type<Data> TYPE = IData.createType(Identifier.of(MythicWorldTweaks.MOD_ID, "trial_spawner_cooldown"));
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
    public void appendData(IDataWriter data, IServerAccessor<TrialSpawnerBlockEntity> accessor, IPluginConfig config) {
        ModConfig.Tweaks.ValueTweaks.TrialSpawnerCooldownOverride trialSpawnerCooldownOverride = ConfigManager.getConfig().tweaks().valueTweaks().trialSpawnerCooldownOverride();
        if (trialSpawnerCooldownOverride.enabled() && trialSpawnerCooldownOverride.wthitIntegration()) {
            int elapsedTicks = accessor.getTarget().getSpawner().getData().mythicworldtweaks$getCompletionElapsedTicks();
            if (elapsedTicks >= 0) {
                int targetCooldownTicks = trialSpawnerCooldownOverride.cooldown() * 20;
                data.add(TYPE, result -> result.add(new Data(targetCooldownTicks - elapsedTicks)));
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
