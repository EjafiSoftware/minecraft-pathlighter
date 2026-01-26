package com.ejafi.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;

public interface WorldProxy {
    boolean isTorch(BlockPos pos);
    HitResult.Type performClip(BlockPos start, BlockPos end);
}
