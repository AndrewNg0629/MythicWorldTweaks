package online.andrew2007.config.configPatterns;

public class TweaksWithParams {
    public FireballAutoDiscard fireballAutoDiscard = null;
    public StuffedShulkerBoxStackLimit stuffedShulkerBoxStackLimit = null;
    public ShulkerBoxNestingLimit shulkerBoxNestingLimit = null;
    public WardenAttributesWeakening wardenAttributesWeakening = null;
    public WardenSonicBoomWeakening wardenSonicBoomWeakening = null;

    public boolean validate() {
        if (this.fireballAutoDiscard == null ||
                this.stuffedShulkerBoxStackLimit == null ||
                this.shulkerBoxNestingLimit == null ||
                this.wardenAttributesWeakening == null ||
                this.wardenSonicBoomWeakening == null) {
            return false;
        } else {
            return this.fireballAutoDiscard.validate() &&
                    this.stuffedShulkerBoxStackLimit.validate() &&
                    this.shulkerBoxNestingLimit.validate() &&
                    this.wardenAttributesWeakening.validate() &&
                    this.wardenSonicBoomWeakening.validate();
        }
    }

    public static class FireballAutoDiscard {
        public Boolean enabled = null;
        public Integer maxLifeTicks = null;

        public boolean validate() {
            if (this.enabled == null || this.maxLifeTicks == null) {
                return false;
            } else {
                return this.maxLifeTicks > 0;
            }
        }
    }

    public static class StuffedShulkerBoxStackLimit {
        public Boolean enabled = null;
        public Integer maxStackSize = null;

        public boolean validate() {
            if (this.enabled == null || maxStackSize == null) {
                return false;
            } else {
                return maxStackSize >= 1;
            }
        }
    }

    public static class ShulkerBoxNestingLimit {
        public Boolean enabled = true;
        public Integer maxLayer = 1;

        public boolean validate() {
            if (this.enabled == null || maxLayer == null) {
                return false;
            } else {
                return this.maxLayer >= 1;
            }
        }
    }

    public static class WardenAttributesWeakening {
        public Boolean enabled = null;
        public Double maxHealth = null;
        public Double knockBackResistance = null;
        public Double meleeAttackDamage = null;
        public Double meleeAttackKnockBack = null;
        public Double idleMovementSpeed = null;
        public Float chasingMovementSpeed = null;
        public Integer attackInterval = null;

        public boolean validate() {
            if (this.enabled == null ||
                    this.maxHealth == null ||
                    this.knockBackResistance == null ||
                    this.meleeAttackDamage == null ||
                    this.idleMovementSpeed == null ||
                    this.chasingMovementSpeed == null ||
                    this.attackInterval == null) {
                return false;
            } else {
                return this.maxHealth > 0 &&
                        this.knockBackResistance >= 0D &&
                        this.knockBackResistance <= 1.0D &&
                        this.meleeAttackDamage >= 0 &&
                        this.meleeAttackKnockBack >= 0 &&
                        this.idleMovementSpeed > 0 &&
                        this.chasingMovementSpeed > 0 &&
                        this.attackInterval > 0;
            }
        }
    }

    public static class WardenSonicBoomWeakening {
        public Boolean enabled = null;
        public Float sonicBoomDamage = null;
        public Integer sonicBoomInterval = null;
        public Double sonicBoomKnockBackFactor = null;

        public boolean validate() {
            if (this.enabled == null || this.sonicBoomDamage == null || this.sonicBoomInterval == null || this.sonicBoomKnockBackFactor == null) {
                return false;
            } else {
                return this.sonicBoomDamage >= 0 &&
                        this.sonicBoomInterval > 0 &&
                        this.sonicBoomInterval < 60 &&
                        this.sonicBoomKnockBackFactor >= 0D;
            }
        }
    }
}
