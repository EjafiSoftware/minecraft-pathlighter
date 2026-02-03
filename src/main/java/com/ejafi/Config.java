package com.ejafi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Pathlighter.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("pathlighter.config.enabled.tooltip")
            .translation("pathlighter.config.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_CRAFT = BUILDER
            .comment("pathlighter.config.enableAutoCraft.tooltip")
            .translation("pathlighter.config.enableAutoCraft")
            .define("enableAutoCraft", true);

    public static final ModConfigSpec.IntValue MAX_COAL_USE = BUILDER
            .comment("pathlighter.config.maxCoalUseLimit.tooltip")
            .translation("pathlighter.config.maxCoalUseLimit")
            .defineInRange("maxCoalUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_STICK_USE = BUILDER
            .comment("pathlighter.config.maxStickUseLimit.tooltip")
            .translation("pathlighter.config.maxStickUseLimit")
            .defineInRange("maxStickUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_TORCHES_LIMIT = BUILDER
            .comment("pathlighter.config.maxTorchesLimit.tooltip")
            .translation("pathlighter.config.maxTorchesLimit")
            .defineInRange("maxTorchesLimit", 64, 1, 640);

    public static final ModConfigSpec.IntValue MIN_LIGHT_LEVEL = BUILDER
            .comment("pathlighter.config.minLightLevel.tooltip")
            .translation("pathlighter.config.minLightLevel")
            .defineInRange("minLightLevel", 0, 0, 15);

    @SuppressWarnings("unchecked")
    private static final ModConfigSpec.ConfigValue<List<String>> BLOCK_BLACKLIST_CONFIG = (ModConfigSpec.ConfigValue<List<String>>) (ModConfigSpec.ConfigValue<?>) BUILDER
            .comment("pathlighter.config.blockBlacklist.tooltip")
            .translation("pathlighter.config.blockBlacklist")
            .defineList("blockBlacklist", List.of("minecraft:glass", "minecraft:ice"), o -> o instanceof String blockStr && ResourceLocation.tryParse(blockStr) != null);

    private static Set<ResourceLocation> cachedBlacklist = Set.of();

    public static boolean isBlacklisted(ResourceLocation blockId) {
        return cachedBlacklist.contains(blockId);
    }

    public static final ModConfigSpec.BooleanValue REQUIRE_MINING_TOOL = BUILDER
            .comment("pathlighter.config.requireMiningTool.tooltip")
            .translation("pathlighter.config.requireMiningTool")
            .define("requireMiningTool", true);

    public static final ModConfigSpec.BooleanValue DISABLE_WHILE_SNEAKING = BUILDER
            .comment("pathlighter.config.disableWhileSneaking.tooltip")
            .translation("pathlighter.config.disableWhileSneaking")
            .define("disableWhileSneaking", true);

    public static final ModConfigSpec.IntValue PLACEMENT_DISTANCE = BUILDER
            .comment("pathlighter.config.placementDistance.tooltip")
            .translation("pathlighter.config.placementDistance")
            .defineInRange("placementDistance", 4, 1, 16);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            cachedBlacklist = BLOCK_BLACKLIST_CONFIG.get().stream()
                    .map(ResourceLocation::parse)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }
}
