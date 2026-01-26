package com.ejafi.strategy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface PlacementStrategy {
    boolean shouldPlace(Player player, BlockPos currentPos, BlockPos lastPos, int interval);
}
