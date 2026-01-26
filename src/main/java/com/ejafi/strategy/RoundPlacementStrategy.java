package com.ejafi.strategy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class RoundPlacementStrategy implements PlacementStrategy {
    @Override
    public boolean shouldPlace(Player player, BlockPos currentPos, BlockPos lastPos, int interval) {
        // Check if we are on a "round" coordinate            
        boolean onXRound = Math.abs(currentPos.getX()) % interval == 0;
        boolean onZRound = Math.abs(currentPos.getZ()) % interval == 0;
        
        if (onXRound || onZRound) {
             // Avoid double placing if they haven't moved far enough from last torch
             return lastPos == null || lastPos.distSqr(currentPos) >= 4; // at least 2 blocks away
        }
        return false;
    }
}
