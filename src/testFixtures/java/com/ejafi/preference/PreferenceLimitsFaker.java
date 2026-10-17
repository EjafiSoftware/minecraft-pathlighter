package com.ejafi.preference;

import com.ejafi.placement.BlockIdFaker;
import java.util.Set;
import net.datafaker.Faker;
import net.minecraft.resources.ResourceLocation;

final class PreferenceLimitsFaker {
    private static final Faker FAKER = new Faker();

    private boolean allowAutoCraft = FAKER.bool().bool();
    private int minimumTorchSpacing =
            FAKER.number().numberBetween(PreferenceValues.MIN_TORCH_SPACING, PreferenceValues.MAX_TORCH_SPACING + 1);
    private Set<ResourceLocation> enforcedGroundBlacklist = BlockIdFaker.generateSet();

    PreferenceLimitsFaker withAllowAutoCraft(boolean allowAutoCraft) {
        this.allowAutoCraft = allowAutoCraft;
        return this;
    }

    PreferenceLimitsFaker withMinimumTorchSpacing(int minimumTorchSpacing) {
        this.minimumTorchSpacing = minimumTorchSpacing;
        return this;
    }

    PreferenceLimitsFaker withEnforcedGroundBlacklist(Set<ResourceLocation> enforcedGroundBlacklist) {
        this.enforcedGroundBlacklist = enforcedGroundBlacklist;
        return this;
    }

    PreferenceLimits generate() {
        return new PreferenceLimits(allowAutoCraft, minimumTorchSpacing, enforcedGroundBlacklist);
    }
}
