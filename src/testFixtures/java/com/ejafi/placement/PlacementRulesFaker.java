package com.ejafi.placement;

import com.ejafi.preference.PreferenceValues;
import java.util.Set;
import net.datafaker.Faker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.lighting.LightEngine;

public final class PlacementRulesFaker {
    private static final Faker FAKER = new Faker();

    private int lightThreshold = FAKER.number().numberBetween(0, LightEngine.MAX_LEVEL + 1);
    private int spacing =
            FAKER.number().numberBetween(PreferenceValues.MIN_TORCH_SPACING, PreferenceValues.MAX_TORCH_SPACING + 1);
    private boolean requireMiningTool = FAKER.bool().bool();
    private boolean pauseWhileSneaking = FAKER.bool().bool();
    private Set<ResourceLocation> groundBlacklist = BlockIdFaker.generateSet();

    public PlacementRulesFaker withLightThreshold(int lightThreshold) {
        this.lightThreshold = lightThreshold;
        return this;
    }

    public PlacementRulesFaker withSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public PlacementRulesFaker withRequireMiningTool(boolean requireMiningTool) {
        this.requireMiningTool = requireMiningTool;
        return this;
    }

    public PlacementRulesFaker withPauseWhileSneaking(boolean pauseWhileSneaking) {
        this.pauseWhileSneaking = pauseWhileSneaking;
        return this;
    }

    public PlacementRulesFaker withGroundBlacklist(Set<ResourceLocation> groundBlacklist) {
        this.groundBlacklist = groundBlacklist;
        return this;
    }

    public PlacementRules generate() {
        return new PlacementRules(lightThreshold, spacing, requireMiningTool, pauseWhileSneaking, groundBlacklist);
    }
}
