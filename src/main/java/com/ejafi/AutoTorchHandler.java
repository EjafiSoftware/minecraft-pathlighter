package com.ejafi;

import com.ejafi.service.CraftingService;
import com.ejafi.service.TorchService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AutoTorchHandler {
    private static final Map<UUID, BlockPos> LAST_TORCH_POS = new ConcurrentHashMap<>();
    private final TorchService torchService;

    public AutoTorchHandler() {
        this.torchService = new TorchService(new CraftingService());
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_TORCH_POS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !Config.ENABLED.get()) return;

        if (player.isSwimming() || player.getAbilities().flying || !player.onGround()) return;

        if (Config.REQUIRE_MINING_TOOL.get()) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (!(mainHandItem.getItem() instanceof DiggerItem)) return;
        }

        if (player.level().getGameTime() % 5 != 0) return;

        BlockPos playerPos = player.blockPosition();
        UUID playerId = player.getUUID();
        BlockPos lastPos = LAST_TORCH_POS.get(playerId);

        if (torchService.shouldPlaceTorch(player, playerPos, lastPos) && torchService.tryPlaceTorch(player, playerPos)) {
            LAST_TORCH_POS.put(playerId, playerPos);
        }
    }
}
