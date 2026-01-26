package com.ejafi.service;

import com.ejafi.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TorchService {
    private final CraftingService craftingService;

    public interface TorchWorld {
        int getLightLevel(BlockPos pos);
    }

    public TorchService(CraftingService craftingService) {
        this.craftingService = craftingService;
    }

    public boolean shouldPlaceTorch(Player player, BlockPos currentPos, BlockPos lastPos) {
        return shouldPlaceTorch(pos -> player.level().getMaxLocalRawBrightness(pos), currentPos, lastPos, Config.MIN_LIGHT_LEVEL.get());
    }

    public boolean shouldPlaceTorch(TorchWorld world, BlockPos currentPos, BlockPos lastPos, int minLightLevel) {
        if (world.getLightLevel(currentPos) > minLightLevel) {
            return false;
        }

        return lastPos == null || !currentPos.closerThan(lastPos, 4);
    }

    public boolean tryPlaceTorch(Player player, BlockPos pos) {
        Level level = player.level();

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        ResourceLocation belowBlockId = BuiltInRegistries.BLOCK.getKey(belowState.getBlock());
        if (Config.BLOCK_BLACKLIST.get().contains(belowBlockId.toString())) {
            return false;
        }

        int torchSlot = craftingService.findTorch(player.getInventory());

        if (torchSlot == -1 && Config.ENABLE_AUTO_CRAFT.get() && craftingService.tryAutoCraftTorches(player)) {
            torchSlot = craftingService.findTorch(player.getInventory());
        }

        if (torchSlot == -1) return false;

        ItemStack torchStack = player.getInventory().getItem(torchSlot);
        BlockState torchState = torchStack.is(Items.SOUL_TORCH) ? Blocks.SOUL_TORCH.defaultBlockState() : Blocks.TORCH.defaultBlockState();

        if (level.getBlockState(pos).isAir() && torchState.canSurvive(level, pos)) {
            level.setBlockAndUpdate(pos, torchState);

            if (!player.getAbilities().instabuild) {
                player.getInventory().removeItem(torchSlot, 1);
            }
            return true;
        }

        return false;
    }
}
