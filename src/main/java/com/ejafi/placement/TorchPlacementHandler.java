package com.ejafi.placement;

import com.ejafi.Pathlighter;
import com.ejafi.preference.PlayerPreferences;
import com.ejafi.supply.TorchSupply;
import com.ejafi.toggle.PathlighterSwitch;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class TorchPlacementHandler {
    static final int TICKS_BETWEEN_CHECKS = 5;
    private static final int RESTORE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
    private static final double GROUND_PROBE_DEPTH = 0.2;
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Pathlighter.MOD_ID);
    private static final Supplier<AttachmentType<TorchTrail>> TORCH_TRAIL = ATTACHMENT_TYPES.register(
            "torch_trail", () -> AttachmentType.builder(() -> TorchTrail.EMPTY).build());

    private TorchPlacementHandler() {}

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
        NeoForge.EVENT_BUS.addListener(TorchPlacementHandler::onPlayerTick);
    }

    static void lightPath(ServerPlayer player) {
        if (player instanceof FakePlayer || !player.isAlive() || !PathlighterSwitch.isOn(player)) {
            return;
        }

        BlockPos spot = findTorchSpot(player);
        PlacementDecision decision = new TorchPlanner(
                        PlayerPreferences.resolve(player).placement())
                .plan(readWalker(player, spot), readSpot(player, spot));
        switch (decision) {
            case PlacementDecision.Place place -> placeTorch(player, place.at());
            case PlacementDecision.Skip unused -> {}
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % TICKS_BETWEEN_CHECKS == 0) {
            lightPath(player);
        }
    }

    private static BlockPos findTorchSpot(ServerPlayer player) {
        return BlockPos.containing(player.getX(), player.getY() - GROUND_PROBE_DEPTH, player.getZ())
                .above();
    }

    private static Walker readWalker(ServerPlayer player, BlockPos spot) {
        return new Walker(
                isOnFoot(player),
                player.isCrouching(),
                player.mayBuild(),
                player.getMainHandItem(),
                spot,
                player.level().dimension(),
                player.getData(TORCH_TRAIL).lastTorch());
    }

    private static boolean isOnFoot(ServerPlayer player) {
        return player.onGround()
                && !player.isPassenger()
                && !player.getAbilities().flying
                && !player.isFallFlying()
                && !player.isSwimming();
    }

    private static Spot readSpot(ServerPlayer player, BlockPos spot) {
        ServerLevel level = player.serverLevel();
        var light = new LightSample(
                level.getBrightness(LightLayer.BLOCK, spot),
                level.getBrightness(LightLayer.SKY, spot),
                level.getSkyDarken());
        ResourceLocation ground =
                BuiltInRegistries.BLOCK.getKey(level.getBlockState(spot.below()).getBlock());
        return new Spot(light, ground, isRoomForTorch(level, spot), level.mayInteract(player, spot));
    }

    private static boolean isRoomForTorch(ServerLevel level, BlockPos spot) {
        if (level.isOutsideBuildHeight(spot)) {
            return false;
        }

        BlockState current = level.getBlockState(spot);
        var context =
                new DirectionalPlaceContext(level, spot, Direction.DOWN, new ItemStack(Items.TORCH), Direction.UP);
        return current.canBeReplaced(context)
                && current.getFluidState().isEmpty()
                && Blocks.TORCH.defaultBlockState().canSurvive(level, spot);
    }

    private static void placeTorch(ServerPlayer player, BlockPos position) {
        var supply = new TorchSupply(PlayerPreferences.resolve(player).crafting());
        Inventory inventory = player.getInventory();
        boolean keepsTorch = player.hasInfiniteMaterials();
        Optional<Block> available = supply.findTorch(inventory);
        if (available.isEmpty() && !keepsTorch) {
            return;
        }

        BlockState torch = available.orElse(Blocks.TORCH).defaultBlockState();
        List<BlockSnapshot> snapshots = captureSnapshots(
                player.serverLevel(), () -> player.serverLevel().setBlock(position, torch, Block.UPDATE_ALL));
        if (snapshots.isEmpty() || !confirmPlacement(player, snapshots.getFirst())) {
            return;
        }

        player.setData(TORCH_TRAIL, new TorchTrail(new TorchMark(player.level().dimension(), position)));
        if (!keepsTorch) {
            supply.takeTorch(inventory);
        }
    }

    private static List<BlockSnapshot> captureSnapshots(ServerLevel level, Runnable change) {
        level.captureBlockSnapshots = true;
        try {
            change.run();
        } finally {
            level.captureBlockSnapshots = false;
        }
        List<BlockSnapshot> snapshots = List.copyOf(level.capturedBlockSnapshots);
        level.capturedBlockSnapshots.clear();
        return snapshots;
    }

    private static boolean confirmPlacement(ServerPlayer player, BlockSnapshot snapshot) {
        ServerLevel level = player.serverLevel();
        BlockPos position = snapshot.getPos();
        BlockState placed = level.getBlockState(position);
        if (EventHooks.onBlockPlace(player, snapshot, Direction.UP)) {
            snapshot.restore(RESTORE_FLAGS);
            return false;
        }

        level.markAndNotifyBlock(
                position,
                level.getChunkAt(position),
                snapshot.getState(),
                placed,
                Block.UPDATE_ALL,
                Block.UPDATE_LIMIT);
        level.gameEvent(GameEvent.BLOCK_PLACE, position, GameEvent.Context.of(player, placed));
        var used = new ItemStack(placed.getBlock());
        CriteriaTriggers.PLACED_BLOCK.trigger(player, position, used);
        player.awardStat(Stats.ITEM_USED.get(used.getItem()));
        return true;
    }
}
