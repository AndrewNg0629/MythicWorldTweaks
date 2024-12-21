package online.andrew2007.config.configPatterns;

public class ItemEditorConfig {
    public static class FoodProperty {
        public Integer nutrition = null;
        public Float saturation = null;

        public boolean validate() {
            if (this.nutrition == null || this.saturation == null) {
                return false;
            } else {
                return this.nutrition >= 0 && this.saturation >= 0;
            }
        }
    }

    public static class ItemProperties {
        public Integer maxCount = null;
        public Integer maxDamage = null;
        public String rarity = null;
        public FoodProperty foodProperty = null;
        public Boolean fireResistance = null;
        public String recipeRemainder = null;
        public boolean validate() {
            if (this.maxCount != null) {
                if (this.maxCount <= 0) {
                    return false;
                }
            }
            if (this.maxDamage != null) {
                return this.maxDamage > 0;
            }
            return true;
        }
    }
}
