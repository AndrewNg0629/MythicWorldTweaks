package online.andrew2007.mythic.modFunctions.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import online.andrew2007.mythic.MythicWorldTweaks;

import java.io.File;
import java.util.Objects;

public class ModMenuHandler implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new Screen(Text.of("No screen")) {
            @Override
            protected void init() {
                Util.getOperatingSystem().open(new File(System.getProperty("user.dir") + "/config/" + MythicWorldTweaks.MOD_ID + "/config.json"));
                Objects.requireNonNull(this.client).setScreen(screen);
            }
        };
    }
}
