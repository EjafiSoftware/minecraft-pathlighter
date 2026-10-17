package com.ejafi.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

record Walker(
        boolean onFoot,
        boolean sneaking,
        boolean mayBuild,
        ItemStack mainHand,
        BlockPos position,
        ResourceKey<Level> dimension,
        @Nullable TorchMark lastTorch) {}
