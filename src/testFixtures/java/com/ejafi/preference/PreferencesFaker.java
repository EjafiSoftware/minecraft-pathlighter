package com.ejafi.preference;

import com.ejafi.placement.PlacementRules;
import com.ejafi.placement.PlacementRulesFaker;
import com.ejafi.supply.CraftingRules;
import com.ejafi.supply.CraftingRulesFaker;

public final class PreferencesFaker {
    private PlacementRules placement = new PlacementRulesFaker().generate();
    private CraftingRules crafting = new CraftingRulesFaker().generate();

    public PreferencesFaker withPlacement(PlacementRules placement) {
        this.placement = placement;
        return this;
    }

    public PreferencesFaker withCrafting(CraftingRules crafting) {
        this.crafting = crafting;
        return this;
    }

    public Preferences generate() {
        return new Preferences(placement, crafting);
    }
}
