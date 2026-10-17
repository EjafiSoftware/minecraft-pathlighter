package com.ejafi;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GameTestConfig {
    private GameTestConfig() {}

    public static void useDefaults() {
        Config.SERVER_SPEC.getValues().entrySet().forEach(entry -> {
            if (entry.getRawValue() instanceof ModConfigSpec.ConfigValue<?> configValue) {
                restoreDefault(configValue);
            }
        });
    }

    public static <T> ConfigOverride override(ModConfigSpec.ConfigValue<T> value, T setting) {
        value.set(setting);
        return () -> restoreDefault(value);
    }

    private static <T> void restoreDefault(ModConfigSpec.ConfigValue<T> value) {
        value.set(value.getDefault());
    }

    /** Puts a setting back to its default when the try-with-resources block that changed it ends. */
    @FunctionalInterface
    public interface ConfigOverride extends AutoCloseable {
        @Override
        void close();
    }
}
