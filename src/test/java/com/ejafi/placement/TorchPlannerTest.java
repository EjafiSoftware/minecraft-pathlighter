package com.ejafi.placement;

import static org.assertj.core.api.Assertions.assertThat;

import com.ejafi.preference.PreferenceValues;
import java.util.Set;
import java.util.stream.Stream;
import net.datafaker.Faker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TorchPlannerTest {
    private static final Faker FAKER = new Faker();

    @Test
    void plan_placesTorchAtFeet_whenWalkingThroughDarkness() {
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker().generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @Test
    void plan_skipsAsNotOnFoot_whenNotWalking() {
        Walker walker = new WalkerFaker().withoutFooting().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker().generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.NOT_ON_FOOT));
    }

    @Test
    void plan_skipsAsSneaking_whenSneakingPausesPlacement() {
        Walker walker = new WalkerFaker().withSneaking().generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withPauseWhileSneaking(true).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.SNEAKING));
    }

    @Test
    void plan_placesTorch_whenSneakingDoesNotPausePlacement() {
        Walker walker = new WalkerFaker().withSneaking().generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withPauseWhileSneaking(false).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @ParameterizedTest
    @MethodSource("listNonMiningTools")
    void plan_skipsAsNoMiningTool_whenToolIsRequiredAndHandHoldsSomethingElse(Item held) {
        Walker walker = new WalkerFaker().withMainHand(new ItemStack(held)).generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withRequireMiningTool(true).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.NO_MINING_TOOL));
    }

    @ParameterizedTest
    @MethodSource("listMiningTools")
    void plan_placesTorch_whenToolIsRequiredAndHandHoldsMiningTool(Item held) {
        Walker walker = new WalkerFaker().withMainHand(new ItemStack(held)).generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withRequireMiningTool(true).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @ParameterizedTest
    @MethodSource("listNonMiningTools")
    void plan_placesTorch_whenToolIsNotRequiredAndHandHoldsSomethingElse(Item held) {
        Walker walker = new WalkerFaker().withMainHand(new ItemStack(held)).generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withRequireMiningTool(false).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @Test
    void plan_skipsAsCannotBuild_whenPlayerMayNotBuild() {
        Walker walker = new WalkerFaker().withoutBuildPermission().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker().generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.CANNOT_BUILD));
    }

    @Test
    void plan_skipsAsCannotBuild_whenSpotIsProtected() {
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker().generate());

        PlacementDecision decision =
                planner.plan(walker, new SpotFaker().withProtection().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.CANNOT_BUILD));
    }

    @Test
    void plan_skipsAsTooBright_whenBlockLightIsAboveThreshold() {
        int threshold = FAKER.number().numberBetween(0, LightEngine.MAX_LEVEL);
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withLightThreshold(threshold).generate());

        PlacementDecision decision = planner.plan(
                walker,
                new SpotFaker().withLight(new LightSample(threshold + 1, 0, 0)).generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.TOO_BRIGHT));
    }

    @Test
    void plan_placesTorch_whenBlockLightEqualsThreshold() {
        int threshold = FAKER.number().numberBetween(0, LightEngine.MAX_LEVEL + 1);
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withLightThreshold(threshold).generate());

        PlacementDecision decision = planner.plan(
                walker,
                new SpotFaker().withLight(new LightSample(threshold, 0, 0)).generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @Test
    void plan_skipsAsTooBright_whenDaylightReachesSpot() {
        int threshold = FAKER.number().numberBetween(0, LightEngine.MAX_LEVEL);
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withLightThreshold(threshold).generate());

        PlacementDecision decision = planner.plan(
                walker,
                new SpotFaker()
                        .withLight(new LightSample(0, LightEngine.MAX_LEVEL, 0))
                        .generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.TOO_BRIGHT));
    }

    @Test
    void plan_placesTorch_whenNightDimsSkylightToThreshold() {
        int skyDarkening = FAKER.number().numberBetween(1, LightEngine.MAX_LEVEL + 1);
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker()
                .withLightThreshold(LightEngine.MAX_LEVEL - skyDarkening)
                .generate());

        PlacementDecision decision = planner.plan(
                walker,
                new SpotFaker()
                        .withLight(new LightSample(0, LightEngine.MAX_LEVEL, skyDarkening))
                        .generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @Test
    void plan_placesTorch_whenBlockLightAndDimmedSkylightBothEqualThreshold() {
        int threshold = FAKER.number().numberBetween(0, LightEngine.MAX_LEVEL + 1);
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(
                new PlacementRulesFaker().withLightThreshold(threshold).generate());

        PlacementDecision decision = planner.plan(
                walker,
                new SpotFaker()
                        .withLight(new LightSample(threshold, LightEngine.MAX_LEVEL, LightEngine.MAX_LEVEL - threshold))
                        .generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(walker.position()));
    }

    @Test
    void plan_skipsAsTooClose_whenLastTorchIsWithinSpacing() {
        int spacing = FAKER.number()
                .numberBetween(PreferenceValues.MIN_TORCH_SPACING + 1, PreferenceValues.MAX_TORCH_SPACING + 1);
        BlockPos position = BlockPosFaker.generate();
        ResourceKey<Level> dimension = DimensionFaker.generate();
        Walker walker = new WalkerFaker()
                .withPosition(position)
                .withDimension(dimension)
                .withLastTorch(new TorchMark(dimension, position.east(spacing - 1)))
                .generate();
        TorchPlanner planner =
                new TorchPlanner(new PlacementRulesFaker().withSpacing(spacing).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.TOO_CLOSE));
    }

    @Test
    void plan_skipsAsTooClose_whenLastTorchIsWithinSpacingDirectlyAbove() {
        int spacing = FAKER.number()
                .numberBetween(PreferenceValues.MIN_TORCH_SPACING + 1, PreferenceValues.MAX_TORCH_SPACING + 1);
        BlockPos position = BlockPosFaker.generate();
        ResourceKey<Level> dimension = DimensionFaker.generate();
        Walker walker = new WalkerFaker()
                .withPosition(position)
                .withDimension(dimension)
                .withLastTorch(new TorchMark(dimension, position.above(spacing - 1)))
                .generate();
        TorchPlanner planner =
                new TorchPlanner(new PlacementRulesFaker().withSpacing(spacing).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.TOO_CLOSE));
    }

    @Test
    void plan_placesTorch_whenLastTorchIsExactlySpacingAway() {
        int spacing = FAKER.number()
                .numberBetween(PreferenceValues.MIN_TORCH_SPACING, PreferenceValues.MAX_TORCH_SPACING + 1);
        BlockPos position = BlockPosFaker.generate();
        ResourceKey<Level> dimension = DimensionFaker.generate();
        Walker walker = new WalkerFaker()
                .withPosition(position)
                .withDimension(dimension)
                .withLastTorch(new TorchMark(dimension, position.east(spacing)))
                .generate();
        TorchPlanner planner =
                new TorchPlanner(new PlacementRulesFaker().withSpacing(spacing).generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(position));
    }

    @Test
    void plan_placesTorch_whenLastTorchIsInAnotherDimension() {
        BlockPos position = BlockPosFaker.generate();
        ResourceKey<Level> dimension = DimensionFaker.generate();
        Walker walker = new WalkerFaker()
                .withPosition(position)
                .withDimension(dimension)
                .withLastTorch(new TorchMark(DimensionFaker.generateOtherThan(dimension), position))
                .generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker().generate());

        PlacementDecision decision = planner.plan(walker, new SpotFaker().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Place(position));
    }

    @Test
    void plan_skipsAsBlacklistedGround_whenStandingOnBlacklistedBlock() {
        ResourceLocation ground = BlockIdFaker.generate();
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker()
                .withGroundBlacklist(Set.of(ground, BlockIdFaker.generate()))
                .generate());

        PlacementDecision decision =
                planner.plan(walker, new SpotFaker().withGround(ground).generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.BLACKLISTED_GROUND));
    }

    @Test
    void plan_skipsAsUnsuitableSpot_whenTorchCannotStandAtFeet() {
        Walker walker = new WalkerFaker().generate();
        TorchPlanner planner = new TorchPlanner(new PlacementRulesFaker().generate());

        PlacementDecision decision =
                planner.plan(walker, new SpotFaker().withoutRoomForTorch().generate());

        assertThat(decision).isEqualTo(new PlacementDecision.Skip(SkipReason.UNSUITABLE_SPOT));
    }

    static Stream<Item> listNonMiningTools() {
        return Stream.of(Items.AIR, Items.DIAMOND_SWORD, Items.BOW, Items.STICK, Items.TORCH);
    }

    static Stream<Item> listMiningTools() {
        return Stream.of(
                Items.WOODEN_PICKAXE, Items.NETHERITE_PICKAXE, Items.IRON_SHOVEL, Items.STONE_AXE, Items.GOLDEN_HOE);
    }
}
