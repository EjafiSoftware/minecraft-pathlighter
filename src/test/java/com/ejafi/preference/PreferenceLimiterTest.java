package com.ejafi.preference;

import static org.assertj.core.api.Assertions.assertThat;

import com.ejafi.placement.BlockIdFaker;
import com.ejafi.placement.PlacementRulesFaker;
import com.ejafi.supply.CraftingRules;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.datafaker.Faker;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class PreferenceLimiterTest {
    private static final Faker FAKER = new Faker();

    @Test
    void limit_turnsOffAutoCraft_whenServerForbidsAutoCraft() {
        Preferences chosen = new PreferencesFaker().generate();
        PreferenceLimits limits =
                new PreferenceLimitsFaker().withAllowAutoCraft(false).generate();

        Preferences limited = PreferenceLimiter.limit(chosen, limits);

        CraftingRules crafting = chosen.crafting();
        assertThat(limited.crafting())
                .isEqualTo(new CraftingRules(false, crafting.coalReserve(), crafting.stickReserve()));
    }

    @Test
    void limit_keepsChosenAutoCraft_whenServerAllowsAutoCraft() {
        Preferences chosen = new PreferencesFaker().generate();
        PreferenceLimits limits =
                new PreferenceLimitsFaker().withAllowAutoCraft(true).generate();

        Preferences limited = PreferenceLimiter.limit(chosen, limits);

        assertThat(limited.crafting()).isEqualTo(chosen.crafting());
    }

    @Test
    void limit_raisesSpacingToMinimum_whenChosenSpacingIsBelowMinimum() {
        int minimum = FAKER.number()
                .numberBetween(PreferenceValues.MIN_TORCH_SPACING + 1, PreferenceValues.MAX_TORCH_SPACING + 1);
        int spacing = FAKER.number().numberBetween(PreferenceValues.MIN_TORCH_SPACING, minimum);
        Preferences chosen = new PreferencesFaker()
                .withPlacement(new PlacementRulesFaker().withSpacing(spacing).generate())
                .generate();
        PreferenceLimits limits =
                new PreferenceLimitsFaker().withMinimumTorchSpacing(minimum).generate();

        Preferences limited = PreferenceLimiter.limit(chosen, limits);

        assertThat(limited.placement().spacing()).isEqualTo(minimum);
    }

    @Test
    void limit_keepsChosenSpacing_whenChosenSpacingMeetsMinimum() {
        int minimum = FAKER.number()
                .numberBetween(PreferenceValues.MIN_TORCH_SPACING, PreferenceValues.MAX_TORCH_SPACING + 1);
        int spacing = FAKER.number().numberBetween(minimum, PreferenceValues.MAX_TORCH_SPACING + 1);
        Preferences chosen = new PreferencesFaker()
                .withPlacement(new PlacementRulesFaker().withSpacing(spacing).generate())
                .generate();
        PreferenceLimits limits =
                new PreferenceLimitsFaker().withMinimumTorchSpacing(minimum).generate();

        Preferences limited = PreferenceLimiter.limit(chosen, limits);

        assertThat(limited.placement().spacing()).isEqualTo(spacing);
    }

    @Test
    void limit_addsEnforcedBlocksToBlacklist_whenServerEnforcesBlacklist() {
        Preferences chosen = new PreferencesFaker().generate();
        Set<ResourceLocation> enforced = BlockIdFaker.generateNonEmptySet();
        PreferenceLimits limits = new PreferenceLimitsFaker()
                .withEnforcedGroundBlacklist(enforced)
                .generate();

        Preferences limited = PreferenceLimiter.limit(chosen, limits);

        assertThat(limited.placement().groundBlacklist())
                .containsExactlyInAnyOrderElementsOf(
                        Stream.concat(chosen.placement().groundBlacklist().stream(), enforced.stream())
                                .collect(Collectors.toUnmodifiableSet()));
    }
}
