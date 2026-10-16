package com.ejafi.placement;

import java.util.List;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

final class TorchPlanner {
    private static final List<ItemAbility> MINING_ABILITIES =
            List.of(ItemAbilities.PICKAXE_DIG, ItemAbilities.SHOVEL_DIG, ItemAbilities.AXE_DIG, ItemAbilities.HOE_DIG);

    private final PlacementRules rules;

    TorchPlanner(PlacementRules rules) {
        this.rules = rules;
    }

    PlacementDecision plan(Walker walker, Spot spot) {
        if (!walker.onFoot()) {
            return new PlacementDecision.Skip(SkipReason.NOT_ON_FOOT);
        }
        if (rules.pauseWhileSneaking() && walker.sneaking()) {
            return new PlacementDecision.Skip(SkipReason.SNEAKING);
        }
        if (rules.requireMiningTool() && !isMiningTool(walker.mainHand())) {
            return new PlacementDecision.Skip(SkipReason.NO_MINING_TOOL);
        }
        if (!walker.mayBuild() || !spot.buildable()) {
            return new PlacementDecision.Skip(SkipReason.CANNOT_BUILD);
        }
        if (measureLight(spot.light()) > rules.lightThreshold()) {
            return new PlacementDecision.Skip(SkipReason.TOO_BRIGHT);
        }
        if (isNearLastTorch(walker)) {
            return new PlacementDecision.Skip(SkipReason.TOO_CLOSE);
        }
        if (rules.groundBlacklist().contains(spot.ground())) {
            return new PlacementDecision.Skip(SkipReason.BLACKLISTED_GROUND);
        }
        if (!spot.torchFits()) {
            return new PlacementDecision.Skip(SkipReason.UNSUITABLE_SPOT);
        }

        return new PlacementDecision.Place(walker.position());
    }

    private static boolean isMiningTool(ItemStack held) {
        return held.getItem() instanceof DiggerItem || MINING_ABILITIES.stream().anyMatch(held::canPerformAction);
    }

    private static int measureLight(LightSample light) {
        return Math.max(light.blockLight(), light.skyLight() - light.skyDarkening());
    }

    private boolean isNearLastTorch(Walker walker) {
        TorchMark lastTorch = walker.lastTorch();
        if (lastTorch == null || !lastTorch.dimension().equals(walker.dimension())) {
            return false;
        }

        return walker.position().closerThan(lastTorch.position(), rules.spacing());
    }
}
