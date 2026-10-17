package com.ejafi;

import com.ejafi.preference.PreferenceValues;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final PreferenceValues PREFERENCES = PreferenceValues.definePreferences(CLIENT_BUILDER);

    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED_BY_DEFAULT = SERVER_BUILDER
            .comment("Whether Pathlighter starts on for players who haven't toggled it themselves")
            .define("enabledByDefault", true);

    public static final PreferenceValues PLAYER_DEFAULTS = PreferenceValues.definePlayerDefaults(SERVER_BUILDER);

    public static final ModConfigSpec.BooleanValue ALLOW_AUTO_CRAFT = SERVER_BUILDER
            .comment(
                    "Whether players can auto-craft torches. When off, no player auto-crafts, whatever their own setting")
            .define("allowAutoCraft", true);

    public static final ModConfigSpec.IntValue MINIMUM_TORCH_SPACING = SERVER_BUILDER
            .comment("Smallest torch spacing a player can use. A player who chooses less gets this spacing")
            .defineInRange(
                    "minimumTorchSpacing",
                    PreferenceValues.MIN_TORCH_SPACING,
                    PreferenceValues.MIN_TORCH_SPACING,
                    PreferenceValues.MAX_TORCH_SPACING);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENFORCED_GROUND_BLACKLIST = SERVER_BUILDER
            .comment("Blocks no player's torches are placed on, in addition to each player's own blacklist")
            .defineListAllowEmpty("enforcedGroundBlacklist", List.of(), () -> "", PreferenceValues::isBlockId);

    static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    private Config() {}
}
