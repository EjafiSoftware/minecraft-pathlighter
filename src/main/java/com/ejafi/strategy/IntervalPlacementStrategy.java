package com.ejafi.strategy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class IntervalPlacementStrategy implements PlacementStrategy {
    @Override
    public boolean shouldPlace(Player player, BlockPos currentPos, BlockPos lastPos, int interval) {
        if (lastPos == null) return true;
        
        // Manhattan distance for grid-based placement
        int dist = Math.abs(currentPos.getX() - lastPos.getX()) + Math.abs(currentPos.getZ() - lastPos.getZ());
        return dist >= interval;
    }
}
