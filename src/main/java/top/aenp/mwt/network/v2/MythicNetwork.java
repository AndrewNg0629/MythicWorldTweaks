package top.aenp.mwt.network.v2;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.aenp.mwt.network.v2.payloads.MythicLoginC2SPayload;
import top.aenp.mwt.network.v2.payloads.MythicLoginS2CPayload;


import java.util.HashMap;

public class MythicNetwork {
    public static final int QUERY_ID = -2147483600;
    public static final HashMap<Identifier, PacketCodec<PacketByteBuf, ? extends MythicLoginS2CPayload>> LOGIN_S2C_CODECS = new HashMap<>();
    public static final HashMap<Identifier, PacketCodec<PacketByteBuf, ? extends MythicLoginC2SPayload>> LOGIN_C2S_CODECS = new HashMap<>();
    public static final HashMap<Identifier, PacketCodec<PacketByteBuf, ? extends CustomPayload>> CUSTOM_PAYLOAD_CODECS = new HashMap<>();
}
