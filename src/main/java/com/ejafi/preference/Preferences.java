package com.ejafi.preference;

import com.ejafi.placement.PlacementRules;
import com.ejafi.supply.CraftingRules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record Preferences(PlacementRules placement, CraftingRules crafting) {
    private static final Codec<PlacementRules> PLACEMENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("light_threshold").forGetter(PlacementRules::lightThreshold),
                    Codec.INT.fieldOf("spacing").forGetter(PlacementRules::spacing),
                    Codec.BOOL.fieldOf("require_mining_tool").forGetter(PlacementRules::requireMiningTool),
                    Codec.BOOL.fieldOf("pause_while_sneaking").forGetter(PlacementRules::pauseWhileSneaking),
                    ResourceLocation.CODEC
                            .listOf()
                            .<Set<ResourceLocation>>xmap(Set::copyOf, List::copyOf)
                            .fieldOf("ground_blacklist")
                            .forGetter(PlacementRules::groundBlacklist))
            .apply(instance, PlacementRules::new));
    private static final Codec<CraftingRules> CRAFTING_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.fieldOf("auto_craft").forGetter(CraftingRules::autoCraft),
                    Codec.INT.fieldOf("coal_reserve").forGetter(CraftingRules::coalReserve),
                    Codec.INT.fieldOf("stick_reserve").forGetter(CraftingRules::stickReserve))
            .apply(instance, CraftingRules::new));
    static final Codec<Preferences> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    PLACEMENT_CODEC.fieldOf("placement").forGetter(Preferences::placement),
                    CRAFTING_CODEC.fieldOf("crafting").forGetter(Preferences::crafting))
            .apply(instance, Preferences::new));
}
