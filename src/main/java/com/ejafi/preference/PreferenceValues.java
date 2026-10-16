package com.ejafi.preference;

import com.ejafi.placement.PlacementRules;
import com.ejafi.supply.CraftingRules;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.common.ModConfigSpec;

public record PreferenceValues(
        ModConfigSpec.IntValue lightThreshold,
        ModConfigSpec.IntValue torchSpacing,
        ModConfigSpec.BooleanValue requireMiningTool,
        ModConfigSpec.BooleanValue pauseWhileSneaking,
        ModConfigSpec.ConfigValue<List<? extends String>> groundBlacklist,
        ModConfigSpec.BooleanValue autoCraft,
        ModConfigSpec.IntValue coalReserve,
        ModConfigSpec.IntValue stickReserve) {
    public static final int MIN_TORCH_SPACING = 1;
    public static final int MAX_TORCH_SPACING = 16;
    private static final List<String> NO_NOTE = List.of();
    private static final List<String> PLAYER_DEFAULTS_NOTE =
            List.of("Applies to players who don't have Pathlighter installed");

    public static PreferenceValues definePreferences(ModConfigSpec.Builder builder) {
        return define(builder, NO_NOTE);
    }

    public static PreferenceValues definePlayerDefaults(ModConfigSpec.Builder builder) {
        return define(builder, PLAYER_DEFAULTS_NOTE);
    }

    public static boolean isBlockId(Object value) {
        return value instanceof String id && ResourceLocation.tryParse(id) != null;
    }

    public Preferences read() {
        return new Preferences(
                new PlacementRules(
                        lightThreshold.get(),
                        torchSpacing.get(),
                        requireMiningTool.get(),
                        pauseWhileSneaking.get(),
                        parseBlockIds(groundBlacklist.get())),
                new CraftingRules(autoCraft.get(), coalReserve.get(), stickReserve.get()));
    }

    static Set<ResourceLocation> parseBlockIds(List<? extends String> ids) {
        return ids.stream().map(ResourceLocation::parse).collect(Collectors.toUnmodifiableSet());
    }

    private static PreferenceValues define(ModConfigSpec.Builder builder, List<String> note) {
        return new PreferenceValues(
                builder.comment(composeComment(
                                "Places a torch where the light at a player's feet is at or below this level (0-15)",
                                note))
                        .defineInRange("lightThreshold", 7, 0, LightEngine.MAX_LEVEL),
                builder.comment(composeComment("Minimum distance in blocks between torches a player places", note))
                        .defineInRange("torchSpacing", 4, MIN_TORCH_SPACING, MAX_TORCH_SPACING),
                builder.comment(composeComment("Only place torches while holding a pickaxe, shovel, axe, or hoe", note))
                        .define("requireMiningTool", true),
                builder.comment(composeComment("Stop placing torches while sneaking", note))
                        .define("pauseWhileSneaking", true),
                builder.comment(composeComment("Blocks torches are never placed on (e.g., 'minecraft:glass')", note))
                        .defineListAllowEmpty(
                                "groundBlacklist",
                                List.of("minecraft:glass", "minecraft:ice"),
                                () -> "",
                                PreferenceValues::isBlockId),
                builder.comment(composeComment(
                                "Craft four torches from one coal or charcoal and one stick when a player runs out",
                                note))
                        .define("autoCraft", true),
                builder.comment(composeComment(
                                "Coal and charcoal that auto-crafting always leaves in the inventory", note))
                        .defineInRange("coalReserve", 0, 0, 64),
                builder.comment(composeComment("Sticks that auto-crafting always leaves in the inventory", note))
                        .defineInRange("stickReserve", 0, 0, 64));
    }

    private static String[] composeComment(String description, List<String> note) {
        return Stream.concat(Stream.of(description), note.stream()).toArray(String[]::new);
    }
}
