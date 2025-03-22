package online.andrew2007.mythic.modFunctions;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public class ItemEntityStuff {
    public static final TrackedData<Boolean> IS_UNDER_PROTECTION = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
}
