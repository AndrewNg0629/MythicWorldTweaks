package top.aenp.mwt.misc;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;

import java.util.Map;
import java.util.UUID;

public class TrialChamberStuff {
    public static final Codec<Map<UUID, Long>> TIMESTAMP_MAP_CODEC = Codec.unboundedMap(Uuids.STRING_CODEC, Codec.LONG);

    public static Text formatCooldown(int remainingTicks) {
        int totalSeconds = remainingTicks / 20;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        String formattedTime = hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
        return Text.translatable("mythicworldtweaks.wthit.tooltip.cooldown", formattedTime).formatted(Formatting.YELLOW);
    }
}
