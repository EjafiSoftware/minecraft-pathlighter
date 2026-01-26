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

    /**
     * Internal logic for checking if a torch should be placed.
     * Decoupled from Minecraft's Level and static Config for better testability.
     */
    public boolean shouldPlaceTorch(TorchWorld world, BlockPos currentPos, BlockPos lastPos, int minLightLevel) {
        // Light level check
        if (world.getLightLevel(currentPos) > minLightLevel) {
            return false;
        }

        // Distance check from last torch placed by this mod to avoid spamming 
        // if player stays in the same spot while light level is low.
        return lastPos == null || !currentPos.closerThan(lastPos, 4);
    }

    public boolean tryPlaceTorch(Player player, BlockPos pos) {
        Level level = player.level();

        // Check if the block below is blacklisted
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        ResourceLocation belowBlockId = BuiltInRegistries.BLOCK.getKey(belowState.getBlock());
        List<String> blacklist = Config.BLOCK_BLACKLIST.get();
        if (blacklist.contains(belowBlockId.toString())) {
            return false;
        }

        int torchSlot = craftingService.findTorch(player.getInventory());

        // Attempts autocrafting when enabled and no torch found
        if (torchSlot == -1 && Config.ENABLE_AUTO_CRAFT.get() && craftingService.tryAutoCraftTorches(player)) {
            torchSlot = craftingService.findTorch(player.getInventory());
        }


        if (torchSlot == -1) return false;

        ItemStack torchStack = player.getInventory().getItem(torchSlot);
        BlockState torchState = Blocks.TORCH.defaultBlockState();
        if (torchStack.is(Items.SOUL_TORCH)) {
            torchState = Blocks.SOUL_TORCH.defaultBlockState();
        }

        // Places torch; removes from inventory unless creative
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
