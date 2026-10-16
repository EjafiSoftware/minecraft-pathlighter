package com.ejafi.placement;

import com.ejafi.Config;
import com.ejafi.GameTestCommands;
import com.ejafi.GameTestConfig;
import com.ejafi.GameTestPlayers;
import com.ejafi.GameTestTemplates;
import com.ejafi.Pathlighter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.datafaker.Faker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Pathlighter.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TorchPlacementGameTest {
    private static final Faker FAKER = new Faker();
    private static final int LIGHT_SETTLE_TICKS = 10;
    private static final int OBSERVER_WATCH_TICKS = 4;
    private static final int FULL_STACK = Items.TORCH.getDefaultMaxStackSize();
    private static final double SOUL_SAND_SURFACE = 0.875;
    private static final BlockPos FIRST_ROOM = new BlockPos(2, 2, 2);
    private static final BlockPos NEARBY_ROOM = new BlockPos(4, 2, 2);
    private static final BlockPos TUNNEL_END = new BlockPos(7, 2, 2);

    private TorchPlacementGameTest() {}

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void on_player_tick_places_torch_when_survival_player_stands_in_darkness(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    carryTorches(miner);

                    for (int tick = 0; tick < TorchPlacementHandler.TICKS_BETWEEN_CHECKS; tick++) {
                        miner.doTick();
                    }

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_torch_for_each_player_when_both_walk_in_darkness(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        buildDarkRoom(helper, NEARBY_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer first = joinMiner(helper, FIRST_ROOM);
                    ServerPlayer second = joinMiner(helper, NEARBY_ROOM);
                    carryTorches(first);
                    carryTorches(second);

                    TorchPlacementHandler.lightPath(first);
                    TorchPlacementHandler.lightPath(second);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertBlockPresent(Blocks.TORCH, NEARBY_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_torch_only_for_player_who_left_it_on_when_other_player_turned_it_off(
            GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        buildDarkRoom(helper, NEARBY_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer leftOn = joinMiner(helper, FIRST_ROOM);
                    ServerPlayer turnedOff = joinMiner(helper, NEARBY_ROOM);
                    carryTorches(leftOn);
                    carryTorches(turnedOff);
                    GameTestCommands.runToggle(helper, turnedOff.createCommandSourceStack());

                    TorchPlacementHandler.lightPath(leftOn);
                    TorchPlacementHandler.lightPath(turnedOff);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertBlockNotPresent(Blocks.TORCH, NEARBY_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_takes_one_torch_from_survival_player_when_placing(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    int carried = carryTorches(miner);

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertValueEqual(
                            miner.getInventory().countItem(Items.TORCH), carried - 1, "torches carried");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_keeps_creative_players_torch_when_placing(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer builder = joinMiner(helper, FIRST_ROOM);
                    builder.setGameMode(GameType.CREATIVE);
                    int carried = carryTorches(builder);

                    TorchPlacementHandler.lightPath(builder);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertValueEqual(builder.getInventory().countItem(Items.TORCH), carried, "torches carried");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_torch_for_creative_player_when_carrying_no_torches(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer builder = joinMiner(helper, FIRST_ROOM);
                    builder.setGameMode(GameType.CREATIVE);

                    TorchPlacementHandler.lightPath(builder);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_for_adventure_player_when_walking_in_darkness(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer adventurer = joinMiner(helper, FIRST_ROOM);
                    adventurer.setGameMode(GameType.ADVENTURE);
                    carryTorches(adventurer);

                    TorchPlacementHandler.lightPath(adventurer);

                    helper.assertBlockNotPresent(Blocks.TORCH, FIRST_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_and_keeps_torch_when_protection_mod_refuses_placement(
            GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    int carried = carryTorches(miner);

                    lightPathRefused(miner);

                    helper.assertBlockNotPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertValueEqual(miner.getInventory().countItem(Items.TORCH), carried, "torches carried");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_keeps_ingredients_when_protection_mod_refuses_placement(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    int coal = FAKER.number().numberBetween(1, FULL_STACK + 1);
                    int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
                    miner.getInventory().add(new ItemStack(Items.COAL, coal));
                    miner.getInventory().add(new ItemStack(Items.STICK, sticks));

                    lightPathRefused(miner);

                    helper.assertValueEqual(miner.getInventory().countItem(Items.TORCH), 0, "torches carried");
                    helper.assertValueEqual(miner.getInventory().countItem(Items.COAL), coal, "coal carried");
                    helper.assertValueEqual(miner.getInventory().countItem(Items.STICK), sticks, "sticks carried");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_leaves_observer_unpowered_when_protection_mod_refuses_placement(
            GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        BlockPos observer = FIRST_ROOM.east();
        helper.setBlock(observer, Blocks.OBSERVER.defaultBlockState().setValue(ObserverBlock.FACING, Direction.WEST));

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    carryTorches(miner);

                    lightPathRefused(miner);
                })
                .thenExecuteFor(
                        OBSERVER_WATCH_TICKS, () -> helper.assertBlockProperty(observer, ObserverBlock.POWERED, false))
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_torch_beside_refused_spot_when_protection_mod_refused_earlier(
            GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkTunnel(helper);
        BlockPos nextStep = FIRST_ROOM.east();
        ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
        carryTorches(miner);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    lightPathRefused(miner);
                    miner.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(nextStep)));

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockPresent(Blocks.TORCH, nextStep);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_for_fake_player_when_machine_stands_in_darkness(
            GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    FakePlayer machine = FakePlayerFactory.getMinecraft(helper.getLevel());
                    equipMiner(machine, Vec3.atBottomCenterOf(helper.absolutePos(FIRST_ROOM)));
                    carryTorches(machine);

                    TorchPlacementHandler.lightPath(machine);

                    helper.assertBlockNotPresent(Blocks.TORCH, FIRST_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_within_spacing_of_last_torch_when_torch_was_removed(
            GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkTunnel(helper);
        BlockPos nextStep = FIRST_ROOM.east();
        ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
        carryTorches(miner);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> placeThenRemoveTorch(helper, miner))
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    miner.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(nextStep)));

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockNotPresent(Blocks.TORCH, nextStep);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_torch_past_spacing_of_last_torch_when_walking_on(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkTunnel(helper);
        ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
        carryTorches(miner);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> placeThenRemoveTorch(helper, miner))
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    miner.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(TUNNEL_END)));

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockPresent(Blocks.TORCH, TUNNEL_END);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_player_is_sneaking(GameTestHelper helper) {
        assertNoTorchFor(helper, player -> player.setPose(Pose.CROUCHING));
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_player_is_flying(GameTestHelper helper) {
        assertNoTorchFor(helper, player -> player.getAbilities().flying = true);
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_player_is_airborne(GameTestHelper helper) {
        assertNoTorchFor(helper, player -> player.setOnGround(false));
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_player_holds_no_mining_tool(GameTestHelper helper) {
        assertNoTorchFor(helper, player -> player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK)));
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_player_is_dead(GameTestHelper helper) {
        assertNoTorchFor(helper, player -> player.setHealth(0));
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_player_rides_a_mount(GameTestHelper helper) {
        assertNoTorchFor(helper, player -> {
            Pig mount = helper.spawn(EntityType.PIG, FIRST_ROOM);
            player.startRiding(mount, true);
            player.setOnGround(true);
        });
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_nothing_when_room_is_lit(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        helper.setBlock(FIRST_ROOM.north(), Blocks.GLOWSTONE);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    carryTorches(miner);

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockNotPresent(Blocks.TORCH, FIRST_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_replaces_short_grass_when_grass_grows_at_feet(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        helper.setBlock(FIRST_ROOM.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(FIRST_ROOM, Blocks.SHORT_GRASS);

        assertTorchPlacedAt(helper, FIRST_ROOM);
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_replaces_snow_layer_when_thin_snow_covers_ground(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        helper.setBlock(FIRST_ROOM, Blocks.SNOW);

        assertTorchPlacedAt(helper, FIRST_ROOM);
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_places_torch_on_soul_sand_when_player_sinks_into_it(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);
        BlockPos soulSand = FIRST_ROOM.below();
        helper.setBlock(soulSand, Blocks.SOUL_SAND);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = GameTestPlayers.joinPlayer(helper);
                    equipMiner(
                            miner,
                            Vec3.atBottomCenterOf(helper.absolutePos(soulSand)).add(0, SOUL_SAND_SURFACE, 0));
                    carryTorches(miner);

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_keeps_torch_when_spot_is_above_build_height(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerLevel level = helper.getLevel();
        BlockPos roof = helper.absolutePos(FIRST_ROOM).atY(level.getMaxBuildHeight() - 1);
        level.setBlockAndUpdate(roof, Blocks.STONE.defaultBlockState());

        try {
            ServerPlayer miner = GameTestPlayers.joinPlayer(helper);
            equipMiner(miner, Vec3.atBottomCenterOf(roof.above()));
            int carried = carryTorches(miner);

            try (GameTestConfig.ConfigOverride brightAnywhere =
                    GameTestConfig.override(Config.PLAYER_DEFAULTS.lightThreshold(), LightEngine.MAX_LEVEL)) {
                TorchPlacementHandler.lightPath(miner);
            }

            helper.assertValueEqual(miner.getInventory().countItem(Items.TORCH), carried, "torches carried");
        } finally {
            level.setBlockAndUpdate(roof, Blocks.AIR.defaultBlockState());
        }
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_crafts_from_offhand_coal_when_carrying_no_torches(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    miner.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.COAL));
                    miner.getInventory().add(new ItemStack(Items.STICK));

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertValueEqual(miner.getInventory().countItem(Items.COAL), 0, "coal carried");
                    helper.assertValueEqual(miner.getInventory().countItem(Items.STICK), 0, "sticks carried");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_announces_block_placement_when_placing_torch(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    carryTorches(miner);
                    var recorder = new PlacementEventRecorder();
                    NeoForge.EVENT_BUS.register(recorder);
                    try {
                        TorchPlacementHandler.lightPath(miner);
                    } finally {
                        NeoForge.EVENT_BUS.unregister(recorder);
                    }

                    helper.assertValueEqual(
                            recorder.getPlacements(), List.of(helper.absolutePos(FIRST_ROOM)), "announced placements");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void light_path_counts_torch_as_used_when_placing(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    carryTorches(miner);

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertValueEqual(
                            miner.getStats().getValue(Stats.ITEM_USED.get(Items.TORCH)), 1, "torches used");
                })
                .thenSucceed();
    }

    private static void assertNoTorchFor(GameTestHelper helper, Consumer<ServerPlayer> condition) {
        GameTestConfig.useDefaults();
        buildDarkRoom(helper, FIRST_ROOM);

        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, FIRST_ROOM);
                    carryTorches(miner);
                    condition.accept(miner);

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockNotPresent(Blocks.TORCH, FIRST_ROOM);
                    helper.assertBlockNotPresent(Blocks.TORCH, FIRST_ROOM.above());
                })
                .thenSucceed();
    }

    private static void assertTorchPlacedAt(GameTestHelper helper, BlockPos feet) {
        helper.startSequence()
                .thenIdle(LIGHT_SETTLE_TICKS)
                .thenExecute(() -> {
                    ServerPlayer miner = joinMiner(helper, feet);
                    carryTorches(miner);

                    TorchPlacementHandler.lightPath(miner);

                    helper.assertBlockPresent(Blocks.TORCH, feet);
                })
                .thenSucceed();
    }

    private static void placeThenRemoveTorch(GameTestHelper helper, ServerPlayer miner) {
        TorchPlacementHandler.lightPath(miner);
        helper.assertBlockPresent(Blocks.TORCH, FIRST_ROOM);
        helper.setBlock(FIRST_ROOM, Blocks.AIR);
    }

    private static void lightPathRefused(ServerPlayer player) {
        PlacementRefuser refuser = new PlacementRefuser();
        NeoForge.EVENT_BUS.register(refuser);
        try {
            TorchPlacementHandler.lightPath(player);
        } finally {
            NeoForge.EVENT_BUS.unregister(refuser);
        }
    }

    private static void buildDarkRoom(GameTestHelper helper, BlockPos feet) {
        fillStone(helper, feet.offset(-1, -1, -1), feet.offset(1, 2, 1));
        helper.setBlock(feet, Blocks.AIR);
        helper.setBlock(feet.above(), Blocks.AIR);
    }

    private static void buildDarkTunnel(GameTestHelper helper) {
        fillStone(helper, FIRST_ROOM.offset(-1, -1, -1), TUNNEL_END.offset(1, 2, 1));
        for (BlockPos feet : BlockPos.betweenClosed(FIRST_ROOM, TUNNEL_END)) {
            helper.setBlock(feet, Blocks.AIR);
            helper.setBlock(feet.above(), Blocks.AIR);
        }
    }

    private static void fillStone(GameTestHelper helper, BlockPos from, BlockPos to) {
        for (BlockPos position : BlockPos.betweenClosed(from, to)) {
            helper.setBlock(position, Blocks.STONE);
        }
    }

    private static ServerPlayer joinMiner(GameTestHelper helper, BlockPos feet) {
        ServerPlayer miner = GameTestPlayers.joinPlayer(helper);
        equipMiner(miner, Vec3.atBottomCenterOf(helper.absolutePos(feet)));
        return miner;
    }

    private static void equipMiner(ServerPlayer player, Vec3 absoluteFeet) {
        player.setGameMode(GameType.SURVIVAL);
        player.moveTo(absoluteFeet);
        player.setOnGround(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
    }

    private static int carryTorches(Player player) {
        int carried = FAKER.number().numberBetween(1, FULL_STACK + 1);
        player.getInventory().add(new ItemStack(Items.TORCH, carried));
        return carried;
    }

    /** Hears block placements the way a sculk sensor or warden would, without needing their ticking chunks. */
    public static final class PlacementEventRecorder {
        private final List<BlockPos> placements = new ArrayList<>();

        @SubscribeEvent
        public void hear(VanillaGameEvent event) {
            if (event.getVanillaEvent().is(GameEvent.BLOCK_PLACE)) {
                placements.add(BlockPos.containing(event.getEventPosition()));
            }
        }

        List<BlockPos> getPlacements() {
            return List.copyOf(placements);
        }
    }

    /** Stands in for a land-claim mod that vetoes every block placement. */
    public static final class PlacementRefuser {
        @SubscribeEvent
        public void refuse(BlockEvent.EntityPlaceEvent event) {
            event.setCanceled(true);
        }
    }
}
