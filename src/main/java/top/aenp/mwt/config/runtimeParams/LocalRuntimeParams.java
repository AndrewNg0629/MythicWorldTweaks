package top.aenp.mwt.config.runtimeParams;

@Deprecated(forRemoval = true)
public record LocalRuntimeParams(
        boolean modEnabled, //Yes
        boolean modDataPackEnabled,
        boolean serverPlaySupportEnabled, //Yes
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
