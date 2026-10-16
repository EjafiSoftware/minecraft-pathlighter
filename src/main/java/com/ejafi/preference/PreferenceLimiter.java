package com.ejafi.preference;

import com.ejafi.placement.PlacementRules;
import com.ejafi.supply.CraftingRules;
import com.google.common.collect.Sets;

final class PreferenceLimiter {
    private PreferenceLimiter() {}

    static Preferences limit(Preferences chosen, PreferenceLimits limits) {
        PlacementRules placement = chosen.placement();
        CraftingRules crafting = chosen.crafting();
        return new Preferences(
                new PlacementRules(
                        placement.lightThreshold(),
                        Math.max(placement.spacing(), limits.minimumTorchSpacing()),
                        placement.requireMiningTool(),
                        placement.pauseWhileSneaking(),
                        Sets.union(placement.groundBlacklist(), limits.enforcedGroundBlacklist())),
                new CraftingRules(
                        crafting.autoCraft() && limits.allowAutoCraft(),
                        crafting.coalReserve(),
                        crafting.stickReserve()));
    }
}
