package com.ejafi.placement;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record PlacementRules(
        int lightThreshold,
        int spacing,
        boolean requireMiningTool,
        boolean pauseWhileSneaking,
        Set<ResourceLocation> groundBlacklist) {
    public PlacementRules {
        groundBlacklist = Set.copyOf(groundBlacklist);
    }
}
