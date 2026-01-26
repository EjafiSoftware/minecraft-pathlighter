package com.ejafi;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = AutoTorch.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Whether the AutoTorch functionality is enabled")
            .translation("autotorch.config.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AUTO_CRAFT = BUILDER
            .comment("Whether to automatically craft torches if out of them")
            .translation("autotorch.config.enableAutoCraft")
            .define("enableAutoCraft", true);

    public static final ModConfigSpec.IntValue MAX_COAL_USE = BUILDER
            .comment("Do not use coal for auto-crafting if player has less than this amount")
            .translation("autotorch.config.maxCoalUseLimit")
            .defineInRange("maxCoalUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_STICK_USE = BUILDER
            .comment("Do not use sticks for auto-crafting if player has less than this amount")
            .translation("autotorch.config.maxStickUseLimit")
            .defineInRange("maxStickUseLimit", 0, 0, 64);

    public static final ModConfigSpec.IntValue MAX_TORCHES_LIMIT = BUILDER
            .comment("Maximum number of torches to keep in inventory via auto-crafting")
            .translation("autotorch.config.maxTorchesLimit")
            .defineInRange("maxTorchesLimit", 64, 1, 640);

    public static final ModConfigSpec.IntValue MIN_LIGHT_LEVEL = BUILDER
            .comment("The minimum light level at the player's position to place a torch. Torches will only be placed if current light is less than or equal to this.")
            .translation("autotorch.config.minLightLevel")
            .defineInRange("minLightLevel", 0, 0, 15);

    @SuppressWarnings("unchecked")
    public static final ModConfigSpec.ConfigValue<List<String>> BLOCK_BLACKLIST = (ModConfigSpec.ConfigValue<List<String>>) (ModConfigSpec.ConfigValue<?>) BUILDER
            .comment("Blocks that AutoTorch should NOT place torches on (e.g., 'minecraft:glass')")
            .translation("autotorch.config.blockBlacklist")
            .defineList(List.of("blockBlacklist"), () -> List.of("minecraft:glass", "minecraft:ice"), () -> "", o -> o instanceof String blockStr && ResourceLocation.tryParse(blockStr) != null);

    public static final ModConfigSpec.BooleanValue REQUIRE_MINING_TOOL = BUILDER
            .comment("Whether to only place torches when holding a mining tool (pickaxe, shovel, etc.)")
            .translation("autotorch.config.requireMiningTool")
            .define("requireMiningTool", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
