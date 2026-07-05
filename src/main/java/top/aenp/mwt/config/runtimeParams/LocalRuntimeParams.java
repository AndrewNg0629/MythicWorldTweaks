package top.aenp.mwt.config.runtimeParams;

public record LocalRuntimeParams(
        boolean modEnabled,
        boolean modDataPackEnabled,
        boolean serverPlaySupportEnabled,
        String serverName,
        boolean modIdValidationEnabled,
        String[] modIdList
) {
    public static LocalRuntimeParams getDefaultInstance() {
        return new LocalRuntimeParams(true, false, true, "A Minecraft Server", false, null);
    }

    public boolean modDataPackEnabled() {
        return modEnabled && modDataPackEnabled;
    }

    public boolean serverPlaySupportEnabled() {
        return modEnabled && serverPlaySupportEnabled;
    }

    public boolean modIdValidationEnabled() {
        return modEnabled && modIdValidationEnabled;
    }
}
