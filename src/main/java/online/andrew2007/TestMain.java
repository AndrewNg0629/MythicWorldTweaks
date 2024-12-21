package online.andrew2007;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;

public class TestMain {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static void main(String[] args) {
        Identifier id = Identifier.of("test", "id");
        MythicWorldTweaks.LOGGER.info(id.toString());
        String sid = GSON.toJson(id);
        MythicWorldTweaks.LOGGER.info(sid);
        Identifier id2 = GSON.fromJson(sid, Identifier.class);
        MythicWorldTweaks.LOGGER.info(id2.toString());
    }
}
