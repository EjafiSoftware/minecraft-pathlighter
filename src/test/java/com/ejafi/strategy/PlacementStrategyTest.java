package com.ejafi.strategy;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlacementStrategyTest {

    @Test
    public void testIntervalPlacement() {
        IntervalPlacementStrategy strategy = new IntervalPlacementStrategy();
        BlockPos lastPos = new BlockPos(0, 0, 0);
        int interval = 10;

        // Test Manhattan distance
        // (9, 0, 0) -> dist 9. Should be false.
        assertFalse(strategy.shouldPlace(null, new BlockPos(9, 0, 0), lastPos, interval));
        // (10, 0, 0) -> dist 10. Should be true.
        assertTrue(strategy.shouldPlace(null, new BlockPos(10, 0, 0), lastPos, interval));
        // (5, 0, 5) -> dist 10. Should be true.
        assertTrue(strategy.shouldPlace(null, new BlockPos(5, 0, 5), lastPos, interval));
        // lastPos is null. Should be true.
        assertTrue(strategy.shouldPlace(null, new BlockPos(0, 0, 0), null, interval));
    }

    @Test
    public void testRoundPlacement() {
        RoundPlacementStrategy strategy = new RoundPlacementStrategy();
        int interval = 10;

        // X is round
        assertTrue(strategy.shouldPlace(null, new BlockPos(10, 0, 5), null, interval));
        // Z is round
        assertTrue(strategy.shouldPlace(null, new BlockPos(7, 0, 20), null, interval));
        // Neither is round
        assertFalse(strategy.shouldPlace(null, new BlockPos(11, 0, 19), null, interval));

        // Test proximity check (distSqr >= 4)
        BlockPos lastPos = new BlockPos(10, 0, 0);
        // (10, 0, 1) -> distSqr 1. Should be false even if X is round.
        assertFalse(strategy.shouldPlace(null, new BlockPos(10, 0, 1), lastPos, interval));
        // (10, 0, 2) -> distSqr 4. Should be true.
        assertTrue(strategy.shouldPlace(null, new BlockPos(10, 0, 2), lastPos, interval));
    }

    @Test
    public void testBothEnabledBehavior() {
        // The current implementation of TorchService.shouldPlaceTorch uses either
        // IntervalPlacementStrategy or RoundPlacementStrategy based on the config.
        // They are mutually exclusive in the configuration.

        // Case 1: Round enabled (interval = 10)
        RoundPlacementStrategy roundStrategy = new RoundPlacementStrategy();
        int roundInterval = 10;
        // Should place at (10, 0, 5) because X is round (10 % 10 == 0)
        assertTrue(roundStrategy.shouldPlace(null, new BlockPos(10, 0, 5), null, roundInterval));

        // Case 2: Interval enabled (interval = 14)
        IntervalPlacementStrategy intervalStrategy = new IntervalPlacementStrategy();
        int torchInterval = 14;
        BlockPos lastPos = new BlockPos(0, 0, 0);
        // Should NOT place at (10, 0, 0) because Manhattan distance is 10 < 14
        assertFalse(intervalStrategy.shouldPlace(null, new BlockPos(10, 0, 0), lastPos, torchInterval));
        // Should place at (14, 0, 0) because Manhattan distance is 14 >= 14
        assertTrue(intervalStrategy.shouldPlace(null, new BlockPos(14, 0, 0), lastPos, torchInterval));
    }
}
