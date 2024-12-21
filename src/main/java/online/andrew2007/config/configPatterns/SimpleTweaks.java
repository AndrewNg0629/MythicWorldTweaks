package online.andrew2007.config.configPatterns;

public class SimpleTweaks {
    public Boolean throwableFireCharge = null;
    public Boolean largeFireCharge = null;
    public Boolean editablePlayerData = null;
    public Boolean creeperBreaksNoBlocks = null;
    public Boolean itemEntityExplosionResistance = null;
    public Boolean shulkerBoxNesting = null;
    public Boolean rideablePlayer = null;
    public Boolean disconnectFallProtection = null;
    public Boolean dispensableTrident = null;
    public Boolean loyalTridentVoidReturn = null;
    public Boolean tridentMultiDamage = null;
    public Boolean survivalTridentPersistence = null;
    public Boolean wardenNoSonicBoom = null;
    public Boolean villagerAlwaysConverts = null;

    public boolean validate() {
        return this.throwableFireCharge != null &&
                this.largeFireCharge != null &&
                this.editablePlayerData != null &&
                this.creeperBreaksNoBlocks != null &&
                this.itemEntityExplosionResistance != null &&
                this.shulkerBoxNesting != null &&
                this.rideablePlayer != null &&
                this.disconnectFallProtection != null &&
                this.dispensableTrident != null &&
                this.loyalTridentVoidReturn != null &&
                this.tridentMultiDamage != null &&
                this.survivalTridentPersistence != null &&
                this.wardenNoSonicBoom != null &&
                this.villagerAlwaysConverts != null;
    }
}
