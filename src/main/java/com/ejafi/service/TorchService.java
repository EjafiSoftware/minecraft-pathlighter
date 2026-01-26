package com.ejafi.service;

import com.ejafi.Config;
import com.ejafi.strategy.IntervalPlacementStrategy;
import com.ejafi.strategy.PlacementStrategy;
import com.ejafi.strategy.RoundPlacementStrategy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TorchService {
    private final CraftingService craftingService;
    private final PlacementStrategy intervalStrategy = new IntervalPlacementStrategy();
    private final PlacementStrategy roundStrategy = new RoundPlacementStrategy();

    public TorchService(CraftingService craftingService) {
        this.craftingService = craftingService;
    }

    public boolean shouldPlaceTorch(Player player, BlockPos currentPos, BlockPos lastPos) {
        Level level = player.level();
        boolean useRound = Config.USE_ROUND_PLACEMENT.get();
        boolean useInterval = Config.USE_INTERVAL_PLACEMENT.get();

        if (!useRound && !useInterval) return false;

        int interval = useRound ? Config.ROUND_INTERVAL.get() : Config.TORCH_INTERVAL.get();

        if (isTorchNearby(player, level, currentPos, interval - 1)) {
            return false;
        }

        PlacementStrategy strategy = useRound ? roundStrategy : intervalStrategy;
        return strategy.shouldPlace(player, currentPos, lastPos, interval);
    }

    /**
     * Checks for nearby torches using world proxy
     */
    public boolean isTorchNearby(Player player, Level level, BlockPos pos, int radius) {
        return isTorchNearby(new WorldProxy() {
            @Override
            public boolean isTorch(BlockPos p) {
                BlockState state = level.getBlockState(p);
                return state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH);
            }

            @Override
            public HitResult.Type performClip(BlockPos start, BlockPos end) {
                Vec3 startVec = Vec3.atCenterOf(start);
                Vec3 endVec = Vec3.atCenterOf(end);

                BlockHitResult result = level.clip(new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                
                if (result.getType() == HitResult.Type.MISS || result.getBlockPos().equals(end)) {
                    return HitResult.Type.MISS;
                }
                return HitResult.Type.BLOCK;
            }
        }, pos, radius);
    }

    public boolean isTorchNearby(WorldProxy world, BlockPos pos, int radius) {
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        for (int y = -2; y <= 2; y++) {
            for (int x = -radius; x <= radius; x++) {
                int zRadius = radius - Math.abs(x);
                for (int z = -zRadius; z <= zRadius; z++) {
                    checkPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (world.isTorch(checkPos)) {
                        if (world.performClip(pos, checkPos) == HitResult.Type.MISS) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean tryPlaceTorch(Player player, BlockPos pos) {
        Level level = player.level();
        int torchSlot = craftingService.findTorch(player.getInventory());

        // Attempts autocrafting when enabled and no torch found
        if (torchSlot == -1 && Config.ENABLE_AUTO_CRAFT.get()) {
            if (craftingService.tryAutoCraftTorches(player)) {
                torchSlot = craftingService.findTorch(player.getInventory());
            }
        }

        if (torchSlot == -1) return false;

        // Places torch; removes from inventory unless creative
        if (level.getBlockState(pos).isAir() && Blocks.TORCH.defaultBlockState().canSurvive(level, pos)) {
            level.setBlockAndUpdate(pos, Blocks.TORCH.defaultBlockState());

            if (!player.isCreative()) {
                player.getInventory().removeItem(torchSlot, 1);
            }
            return true;
        }

        return false;
    }
}
