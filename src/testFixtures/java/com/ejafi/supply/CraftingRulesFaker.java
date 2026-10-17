package com.ejafi.supply;

import net.datafaker.Faker;

public final class CraftingRulesFaker {
    private static final Faker FAKER = new Faker();
    private static final int MAX_RESERVE = 8;

    private boolean autoCraft = true;
    private int coalReserve = FAKER.number().numberBetween(0, MAX_RESERVE + 1);
    private int stickReserve = FAKER.number().numberBetween(0, MAX_RESERVE + 1);

    public CraftingRulesFaker withoutAutoCraft() {
        this.autoCraft = false;
        return this;
    }

    public CraftingRulesFaker withCoalReserve(int coalReserve) {
        this.coalReserve = coalReserve;
        return this;
    }

    public CraftingRulesFaker withStickReserve(int stickReserve) {
        this.stickReserve = stickReserve;
        return this;
    }

    public CraftingRulesFaker withoutReserves() {
        this.coalReserve = 0;
        this.stickReserve = 0;
        return this;
    }

    public CraftingRules generate() {
        return new CraftingRules(autoCraft, coalReserve, stickReserve);
    }
}
