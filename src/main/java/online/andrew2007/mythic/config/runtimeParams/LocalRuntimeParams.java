package online.andrew2007.mythic.config.runtimeParams;

public record LocalRuntimeParams(
        boolean modEnabled,
        boolean modDataPackEnabled,
        boolean serverPlaySupportEnabled,
        boolean modIdValidationEnabled,
        String[] modIdList
) {
    public static LocalRuntimeParams getDefaultInstance() {
        return new LocalRuntimeParams(true, false, true, false, null);
    }
}
