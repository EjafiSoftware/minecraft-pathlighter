package com.ejafi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = AutoTorch.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enable or disable AutoTorch functionality")
            .translation("autotorch.config.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_CRAFT = BUILDER
            .comment("Automatically craft torches when running low")
            .translation("autotorch.config.enableAutoCraft")
            .define("enableAutoCraft", true);

    public static final ModConfigSpec.IntValue MAX_COAL_USE = BUILDER
            .comment("Minimum coal count to keep in inventory")
            .translation("autotorch.config.maxCoalUseLimit")
            .defineInRange("maxCoalUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_STICK_USE = BUILDER
            .comment("Minimum stick count to keep in inventory")
            .translation("autotorch.config.maxStickUseLimit")
            .defineInRange("maxStickUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_TORCHES_LIMIT = BUILDER
            .comment("Maximum torches to keep in inventory via auto-crafting")
            .translation("autotorch.config.maxTorchesLimit")
            .defineInRange("maxTorchesLimit", 64, 1, 640);

    public static final ModConfigSpec.IntValue MIN_LIGHT_LEVEL = BUILDER
            .comment("Light level threshold for placing torches (0-15)")
            .translation("autotorch.config.minLightLevel")
            .defineInRange("minLightLevel", 0, 0, 15);

    @SuppressWarnings("unchecked")
    private static final ModConfigSpec.ConfigValue<List<String>> BLOCK_BLACKLIST_CONFIG = (ModConfigSpec.ConfigValue<List<String>>) (ModConfigSpec.ConfigValue<?>) BUILDER
            .comment("Blocks to ignore when placing torches (e.g., 'minecraft:glass')")
            .translation("autotorch.config.blockBlacklist")
            .defineList("blockBlacklist", List.of("minecraft:glass", "minecraft:ice"), o -> o instanceof String blockStr && ResourceLocation.tryParse(blockStr) != null);

    private static Set<ResourceLocation> cachedBlacklist = Set.of();

    public static boolean isBlacklisted(ResourceLocation blockId) {
        return cachedBlacklist.contains(blockId);
    }

    public static final ModConfigSpec.BooleanValue REQUIRE_MINING_TOOL = BUILDER
            .comment("Only place torches when holding a mining tool (pickaxe, shovel, etc.)")
            .translation("autotorch.config.requireMiningTool")
            .define("requireMiningTool", true);

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
