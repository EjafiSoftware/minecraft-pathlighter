package com.ejafi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = AutoTorch.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static boolean switching = false;

    @SubscribeEvent
    static void onConfigEvent(ModConfigEvent event) {
        if (switching) return;
        switching = true;
        try {
            if (USE_ROUND_PLACEMENT.get() && USE_INTERVAL_PLACEMENT.get()) {
                if (event.getConfig().getType() == ModConfig.Type.COMMON) {
                    USE_INTERVAL_PLACEMENT.set(false);
                    USE_INTERVAL_PLACEMENT.save();
                }
            }
        } finally {
            switching = false;
        }
    }

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Whether the AutoTorch functionality is enabled")
            .translation("autotorch.config.enabled")
            .define("enabled", true);

    public static final ModConfigSpec.IntValue TORCH_INTERVAL = BUILDER
            .comment("The interval in blocks between placing torches")
            .translation("autotorch.config.torchInterval")
            .defineInRange("torchInterval", 10, 1, 100);

    public static final ModConfigSpec.BooleanValue USE_ROUND_PLACEMENT = BUILDER
            .comment("Whether to start placing torches on round numbers of blocks")
            .translation("autotorch.config.useRoundPlacement")
            .define("useRoundPlacement", true);

    public static final ModConfigSpec.BooleanValue USE_INTERVAL_PLACEMENT = BUILDER
            .comment("Whether to place torches based on distance from last torch")
            .translation("autotorch.config.useIntervalPlacement")
            .define("useIntervalPlacement", false);

    public static final ModConfigSpec.IntValue ROUND_INTERVAL = BUILDER
            .comment("The round number interval (e.g., 10 means place at x/z coordinates ending in 0)")
            .translation("autotorch.config.roundInterval")
            .defineInRange("roundInterval", 10, 1, 100);

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

    static final ModConfigSpec SPEC = BUILDER.build();
}
