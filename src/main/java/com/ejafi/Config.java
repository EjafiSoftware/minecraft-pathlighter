package com.ejafi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = AutoTorch.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("autotorch.config.enabled.tooltip")
            .translation("autotorch.config.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_CRAFT = BUILDER
            .comment("autotorch.config.enableAutoCraft.tooltip")
            .translation("autotorch.config.enableAutoCraft")
            .define("enableAutoCraft", true);

    public static final ModConfigSpec.IntValue MAX_COAL_USE = BUILDER
            .comment("autotorch.config.maxCoalUseLimit.tooltip")
            .translation("autotorch.config.maxCoalUseLimit")
            .defineInRange("maxCoalUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_STICK_USE = BUILDER
            .comment("autotorch.config.maxStickUseLimit.tooltip")
            .translation("autotorch.config.maxStickUseLimit")
            .defineInRange("maxStickUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_TORCHES_LIMIT = BUILDER
            .comment("autotorch.config.maxTorchesLimit.tooltip")
            .translation("autotorch.config.maxTorchesLimit")
            .defineInRange("maxTorchesLimit", 64, 1, 640);

    public static final ModConfigSpec.IntValue MIN_LIGHT_LEVEL = BUILDER
            .comment("autotorch.config.minLightLevel.tooltip")
            .translation("autotorch.config.minLightLevel")
            .defineInRange("minLightLevel", 0, 0, 15);

    @SuppressWarnings("unchecked")
    private static final ModConfigSpec.ConfigValue<List<String>> BLOCK_BLACKLIST_CONFIG = (ModConfigSpec.ConfigValue<List<String>>) (ModConfigSpec.ConfigValue<?>) BUILDER
            .comment("autotorch.config.blockBlacklist.tooltip")
            .translation("autotorch.config.blockBlacklist")
            .defineList("blockBlacklist", List.of("minecraft:glass", "minecraft:ice"), o -> o instanceof String blockStr && ResourceLocation.tryParse(blockStr) != null);

    private static Set<ResourceLocation> cachedBlacklist = Set.of();

    public static boolean isBlacklisted(ResourceLocation blockId) {
        return cachedBlacklist.contains(blockId);
    }

    public static final ModConfigSpec.BooleanValue REQUIRE_MINING_TOOL = BUILDER
            .comment("autotorch.config.requireMiningTool.tooltip")
            .translation("autotorch.config.requireMiningTool")
            .define("requireMiningTool", true);

    public static final ModConfigSpec.BooleanValue DISABLE_WHILE_SNEAKING = BUILDER
            .comment("autotorch.config.disableWhileSneaking.tooltip")
            .translation("autotorch.config.disableWhileSneaking")
            .define("disableWhileSneaking", true);

    public static final ModConfigSpec.IntValue PLACEMENT_DISTANCE = BUILDER
            .comment("autotorch.config.placementDistance.tooltip")
            .translation("autotorch.config.placementDistance")
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
