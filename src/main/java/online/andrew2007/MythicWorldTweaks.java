package online.andrew2007;

import net.fabricmc.api.ModInitializer;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import online.andrew2007.itemCenter.ItemInitializer;
import online.andrew2007.util.EnvironmentDetection;
import online.andrew2007.itemCenter.ItemEditor;
import online.andrew2007.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MythicWorldTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MythicWorldTweaks");
    public static final String MOD_ID = "mythicworldtweaks";

    public static void newTestFlight() {
        ItemEditor s1 = ItemEditor.getInstance(Items.DIAMOND);
        ItemEditor s2 = ItemEditor.getInstance(Items.ELYTRA);
        s1.setMaxStackSize(127);
        s1.setRarity(Rarity.EPIC);
        FoodComponent fc = (new FoodComponent.Builder()).nutrition(5).saturationModifier(1.2F).build();
        s1.setFoodComponent(fc);
        s2.setMaxDamage(900);
        s2.setFireResistance(true);
        s1.apply();
        s2.apply();
    }

    @Override
    public void onInitialize() {
        newTestFlight();
        ItemInitializer.generalInitialization();
        MiscUtil.registerMWTEvents();
        LOGGER.info("MythicWorld Tweaks initialized.");
        if (EnvironmentDetection.isYarn()) LOGGER.info("Running in debug environment.");
        else LOGGER.info("Running in production environment.");
        if (EnvironmentDetection.isPhyClient()) LOGGER.info("Running on physical client.");
        else LOGGER.info("Running on physical server.");
    }
}