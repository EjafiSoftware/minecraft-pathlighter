package com.ejafi;

import com.ejafi.service.CraftingService;
import com.ejafi.service.TorchData;
import com.ejafi.service.TorchService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PathlighterHandler {
    private static final CraftingService CRAFTING_SERVICE = new CraftingService();
    private static final TorchService TORCH_SERVICE = new TorchService(CRAFTING_SERVICE);

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !Config.ENABLED.get()) return;

        if (player.isSwimming() || player.getAbilities().flying || !player.onGround()) return;
        if (Config.DISABLE_WHILE_SNEAKING.get() && player.isCrouching()) return;

        // Spread the load: only process if the game time matches the player's unique offset
        if ((player.level().getGameTime() + player.getUUID().getLeastSignificantBits()) % 5 != 0) return;

        if (Config.REQUIRE_MINING_TOOL.get()) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (!(mainHandItem.getItem() instanceof DiggerItem)) return;
        }

        BlockPos playerPos = player.blockPosition();
        TorchData data = player.getData(Pathlighter.TORCH_DATA);
        BlockPos lastPos = data.getLastTorchPos();

        if (TORCH_SERVICE.shouldPlaceTorch(player, playerPos, lastPos) && TORCH_SERVICE.tryPlaceTorch(player, playerPos)) {
            data.setLastTorchPos(playerPos.immutable());
        }
    }
}
