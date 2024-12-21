package online.andrew2007.config.configPatterns;

import java.util.ArrayList;

public class FundamentalConfig {
    public Boolean modEnabled = null;
    public Boolean serverPlaySupport = null;
    public ClasspathValidationConfig classpathValidation = null;

    public boolean validate() {
        if (this.modEnabled == null || this.serverPlaySupport == null || this.classpathValidation == null) {
            return false;
        } else {
            return this.classpathValidation.validate();
        }
    }

    public static class ClasspathValidationConfig {
        public Boolean enabled = null;
        public ArrayList<String> classpathList = null;

        public boolean validate() {
            return this.enabled != null && this.classpathList != null;
        }
    }
}
