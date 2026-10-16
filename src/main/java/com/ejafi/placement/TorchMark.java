package com.ejafi.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

record TorchMark(ResourceKey<Level> dimension, BlockPos position) {}
