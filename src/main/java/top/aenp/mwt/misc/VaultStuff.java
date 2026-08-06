package top.aenp.mwt.misc;

import com.mojang.serialization.Codec;
import net.minecraft.util.Uuids;

import java.util.Map;
import java.util.UUID;

public class VaultStuff {
    public static final Codec<Map<UUID, Integer>> COOLDOWN_MAP_CODEC = Codec.unboundedMap(Uuids.STRING_CODEC, Codec.INT);
}
