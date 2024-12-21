package online.andrew2007.util;

import online.andrew2007.itemCenter.ItemInitializer;

import java.util.function.Consumer;

public enum MythicWorldTweaksToggle {
    THROWABLE_FIRE_CHARGE(false, null),
    LARGE_FIRE_CHARGE(false, null),
    CAN_EDIT_PLAYER_DATA(false, null),
    CREEPER_CANT_BREAK_BLOCKS(false, null),
    ITEM_ENTITY_EXPLOSION_RESISTANCE(false, null),
    FIREBALL_AGE_TRACK(false, null),
    ITEM_MODIFICATIONS(false, enabled -> {
        if (enabled) {
            ItemInitializer.applyItemModifications();
        } else {
            ItemInitializer.revertItemModifications();
        }
    }),
    LIMIT_STUFFED_SHULKER_BOX_STACKING(false, null),
    SHULKER_BOX_NESTING(false, null),
    SHULKER_BOX_NESTING_LIMIT(false, null),
    RIDE_COMMAND_PLAYER_VEHICLE(false, null),
    TRIDENT_VOID_RETURN(false, null),
    TRIDENT_CAN_DAMAGE_MULTIPLE_TIMES(false, null),
    PLAYER_THROWN_TRIDENT_PERSISTENCE(false, null),
    WARDEN_ATTRIBUTES_WEAKEN(false, enabled -> {
        MiscUtil.modifyWardenAttributes(enabled);
        WardenEntityTrack.wardenRefresh();
    }),
    WARDEN_NO_SONIC_BOOM(false, enabled -> WardenEntityTrack.wardenRefresh()),
    WARDEN_SONIC_BOOM_WEAKEN(false, null),
    VILLAGER_ALWAYS_CONVERTS(false, null),
    DISPENSABLE_TRIDENT(false, null);
    private final Consumer<Boolean> toggleAction;
    private boolean enabled;

    MythicWorldTweaksToggle(boolean enabled, Consumer<Boolean> toggleAction) {
        this.enabled = enabled;
        this.toggleAction = toggleAction;
        if (this.toggleAction != null) {
            this.toggleAction.accept(this.enabled);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.toggleAction != null) {
            this.toggleAction.accept(this.enabled);
        }
    }
}
