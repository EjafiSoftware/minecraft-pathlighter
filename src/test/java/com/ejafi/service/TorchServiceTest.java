package com.ejafi.service;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TorchServiceTest {

    private TorchService torchService;
    private TorchService.TorchWorld world;

    @BeforeEach
    void setup() {
        CraftingService craftingService = mock(CraftingService.class);
        torchService = new TorchService(craftingService);
        world = mock(TorchService.TorchWorld.class);
    }

    @Test
    void testShouldPlaceTorch_LightLevelTooHigh() {
        BlockPos pos = new BlockPos(0, 64, 0);
        
        when(world.getLightLevel(pos)).thenReturn(1);

        assertFalse(torchService.shouldPlaceTorch(world, pos, null, 0));
    }

    @Test
    void testShouldPlaceTorch_LightLevelOk() {
        BlockPos pos = new BlockPos(0, 64, 0);
        
        when(world.getLightLevel(pos)).thenReturn(0);

        assertTrue(torchService.shouldPlaceTorch(world, pos, null, 0));
    }

    @Test
    void testShouldPlaceTorch_TooCloseToLastPos() {
        BlockPos pos = new BlockPos(0, 64, 0);
        BlockPos lastPos = new BlockPos(1, 64, 0);
        
        when(world.getLightLevel(pos)).thenReturn(0);

        assertFalse(torchService.shouldPlaceTorch(world, pos, lastPos, 0));
    }

    @Test
    void testShouldPlaceTorch_FarEnoughFromLastPos() {
        BlockPos pos = new BlockPos(0, 64, 0);
        BlockPos lastPos = new BlockPos(10, 64, 0);
        
        when(world.getLightLevel(pos)).thenReturn(0);

        assertTrue(torchService.shouldPlaceTorch(world, pos, lastPos, 0));
    }
}
