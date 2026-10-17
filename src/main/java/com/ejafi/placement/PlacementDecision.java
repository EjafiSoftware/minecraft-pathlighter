package com.ejafi.placement;

import net.minecraft.core.BlockPos;

sealed interface PlacementDecision {
    record Place(BlockPos at) implements PlacementDecision {}

    record Skip(SkipReason reason) implements PlacementDecision {}
}
