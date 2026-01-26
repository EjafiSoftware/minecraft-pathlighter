package com.ejafi;

import com.ejafi.service.CraftingService;
import com.ejafi.service.TorchService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutoTorchHandler {
    private static final Map<UUID, BlockPos> LAST_TORCH_POS = new HashMap<>();
    private final TorchService torchService;

    public AutoTorchHandler() {
        this.torchService = new TorchService(new CraftingService());
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) return;
        if (!Config.ENABLED.get()) return;

        Player player = event.getEntity();

        // Skip if player is swimming, flying, or not on ground
        if (player.isSwimming() || player.getAbilities().flying || !player.onGround()) return;

        // Check if player is holding a mining tool
        if (Config.REQUIRE_MINING_TOOL.get()) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (!(mainHandItem.getItem() instanceof DiggerItem)) return;
        }

        // Only check every 5 ticks to save performance, unless they moved significantly
        long gameTime = player.level().getGameTime();
        if (gameTime % 5 != 0) {
             // We could still check if they moved a full block, but 5 ticks is usually enough for most movement
             // and saves a lot of processing.
             return;
        }

        BlockPos playerPos = player.blockPosition();
        UUID playerId = player.getUUID();
        BlockPos lastPos = LAST_TORCH_POS.get(playerId);

        if (torchService.shouldPlaceTorch(player, playerPos, lastPos) && torchService.tryPlaceTorch(player, playerPos)) {
                LAST_TORCH_POS.put(playerId, playerPos);
            }

    }
}
