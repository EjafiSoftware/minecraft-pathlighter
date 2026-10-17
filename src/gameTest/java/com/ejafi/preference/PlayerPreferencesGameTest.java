package com.ejafi.preference;

import com.ejafi.Config;
import com.ejafi.GameTestConfig;
import com.ejafi.GameTestPlayers;
import com.ejafi.GameTestTemplates;
import com.ejafi.Pathlighter;
import com.ejafi.placement.BlockIdFaker;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.datafaker.Faker;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.lighting.LightEngine;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Pathlighter.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PlayerPreferencesGameTest {
    private static final Faker FAKER = new Faker();

    private PlayerPreferencesGameTest() {}

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void resolve_follows_server_defaults_when_player_sent_no_preferences(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        int threshold = FAKER.number().numberBetween(0, LightEngine.MAX_LEVEL + 1);

        try (GameTestConfig.ConfigOverride changedDefault =
                GameTestConfig.override(Config.PLAYER_DEFAULTS.lightThreshold(), threshold)) {
            helper.assertValueEqual(
                    PlayerPreferences.resolve(player).placement().lightThreshold(), threshold, "light threshold");
        }
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void resolve_follows_players_choice_when_player_sent_preferences(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        Preferences chosen = new PreferencesFaker().generate();

        GameTestPlayers.sendToServer(player, new PreferencesPayload(chosen));

        helper.succeedWhen(() -> helper.assertValueEqual(PlayerPreferences.resolve(player), chosen, "preferences"));
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void resolve_keeps_players_choice_after_respawn_when_player_dies(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        Preferences chosen = new PreferencesFaker().generate();
        GameTestPlayers.sendToServer(player, new PreferencesPayload(chosen));

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertValueEqual(PlayerPreferences.resolve(player), chosen, "preferences"))
                .thenExecute(() -> {
                    ServerPlayer respawned = helper.getLevel()
                            .getServer()
                            .getPlayerList()
                            .respawn(player, false, Entity.RemovalReason.KILLED);

                    helper.assertValueEqual(PlayerPreferences.resolve(respawned), chosen, "preferences after respawn");
                })
                .thenSucceed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void resolve_turns_off_auto_craft_when_server_forbids_auto_craft(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);

        try (GameTestConfig.ConfigOverride forbidden = GameTestConfig.override(Config.ALLOW_AUTO_CRAFT, false)) {
            helper.assertValueEqual(PlayerPreferences.resolve(player).crafting().autoCraft(), false, "auto-craft");
        }
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void resolve_raises_spacing_to_server_minimum_when_chosen_spacing_is_below_it(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        int minimum = FAKER.number()
                .numberBetween(PreferenceValues.MIN_TORCH_SPACING + 1, PreferenceValues.MAX_TORCH_SPACING + 1);
        int spacing = FAKER.number().numberBetween(PreferenceValues.MIN_TORCH_SPACING, minimum);

        try (GameTestConfig.ConfigOverride chosenSpacing =
                        GameTestConfig.override(Config.PLAYER_DEFAULTS.torchSpacing(), spacing);
                GameTestConfig.ConfigOverride raisedMinimum =
                        GameTestConfig.override(Config.MINIMUM_TORCH_SPACING, minimum)) {
            helper.assertValueEqual(
                    PlayerPreferences.resolve(player).placement().spacing(), minimum, "spacing");
        }
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void resolve_adds_enforced_blocks_to_blacklist_when_server_enforces_blacklist(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        ResourceLocation enforced = BlockIdFaker.generate();
        Set<ResourceLocation> chosen = Config.PLAYER_DEFAULTS.read().placement().groundBlacklist();

        try (GameTestConfig.ConfigOverride enforcedBlacklist =
                GameTestConfig.override(Config.ENFORCED_GROUND_BLACKLIST, List.of(enforced.toString()))) {
            helper.assertValueEqual(
                    PlayerPreferences.resolve(player).placement().groundBlacklist(),
                    Stream.concat(chosen.stream(), Stream.of(enforced)).collect(Collectors.toUnmodifiableSet()),
                    "ground blacklist");
        }
        helper.succeed();
    }
}
