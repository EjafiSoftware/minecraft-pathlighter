package com.ejafi.service;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TorchServiceTest {

    private TorchService torchService;
    private CraftingService craftingService;
    private WorldProxy world;

    @BeforeEach
    public void setup() {
        craftingService = mock(CraftingService.class);
        torchService = new TorchService(craftingService);
        world = mock(WorldProxy.class);
    }

    @Test
    public void testIsTorchNearby_NoTorch() {
        BlockPos pos = new BlockPos(0, 0, 0);
        int radius = 5;

        when(world.isTorch(any(BlockPos.class))).thenReturn(false);

        assertFalse(torchService.isTorchNearby(world, pos, radius));
    }

    @Test
    public void testIsTorchNearby_TorchWithLineOfSight() {
        BlockPos playerPos = new BlockPos(0, 0, 0);
        // Position should be within Manhattan distance and Y range
        BlockPos torchPos = new BlockPos(2, 0, 0); 
        int radius = 5;

        // Use a wrapper to handle the MutableBlockPos check if necessary, 
        // but Mockito should match the values if we use any(BlockPos.class) or specific values.
        // Actually, the implementation uses checkPos.set() so the SAME object is passed.
        // We need to be careful with Mockito and mutable objects.
        
        when(world.isTorch(any(BlockPos.class))).thenAnswer(invocation -> {
            BlockPos pos = invocation.getArgument(0);
            return pos.getX() == 2 && pos.getY() == 0 && pos.getZ() == 0;
        });
        when(world.performClip(eq(playerPos), any(BlockPos.class))).thenReturn(HitResult.Type.MISS);

        assertTrue(torchService.isTorchNearby(world, playerPos, radius));
    }

    @Test
    public void testIsTorchNearby_TorchObstructed() {
        BlockPos playerPos = new BlockPos(0, 0, 0);
        int radius = 5;

        when(world.isTorch(any(BlockPos.class))).thenAnswer(invocation -> {
            BlockPos pos = invocation.getArgument(0);
            return pos.getX() == 2 && pos.getY() == 0 && pos.getZ() == 0;
        });
        when(world.performClip(eq(playerPos), any(BlockPos.class))).thenReturn(HitResult.Type.BLOCK);

        assertFalse(torchService.isTorchNearby(world, playerPos, radius));
    }
}
