package com.ejafi.service;

import net.minecraft.core.BlockPos;

public class TorchData {
    private BlockPos lastTorchPos;

    public BlockPos getLastTorchPos() {
        return lastTorchPos;
    }

    public void setLastTorchPos(BlockPos lastTorchPos) {
        this.lastTorchPos = lastTorchPos;
    }
}
