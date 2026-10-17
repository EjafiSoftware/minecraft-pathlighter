package com.ejafi.toggle;

import com.ejafi.Config;
import com.ejafi.GameTestCommands;
import com.ejafi.GameTestConfig;
import com.ejafi.GameTestPlayers;
import com.ejafi.GameTestTemplates;
import com.ejafi.Pathlighter;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Pathlighter.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PathlighterSwitchGameTest {
    private static final int NO_PERMISSION = 0;

    private PathlighterSwitchGameTest() {}

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void toggle_command_switches_only_its_caller_when_two_players_are_online(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer caller = GameTestPlayers.joinPlayer(helper);
        ServerPlayer bystander = GameTestPlayers.joinPlayer(helper);
        boolean callerWasOn = PathlighterSwitch.isOn(caller);
        boolean bystanderWasOn = PathlighterSwitch.isOn(bystander);

        GameTestCommands.runToggle(helper, caller.createCommandSourceStack());

        helper.assertValueEqual(PathlighterSwitch.isOn(caller), !callerWasOn, "caller switch");
        helper.assertValueEqual(PathlighterSwitch.isOn(bystander), bystanderWasOn, "bystander switch");
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void toggle_command_switches_caller_when_caller_has_no_operator_permission(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer caller = GameTestPlayers.joinPlayer(helper);
        boolean wasOn = PathlighterSwitch.isOn(caller);

        GameTestCommands.runToggle(helper, caller.createCommandSourceStack().withPermission(NO_PERMISSION));

        helper.assertValueEqual(PathlighterSwitch.isOn(caller), !wasOn, "caller switch");
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void is_on_keeps_players_choice_after_respawn_when_player_dies(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        GameTestCommands.runToggle(helper, player.createCommandSourceStack());
        boolean stateBeforeDeath = PathlighterSwitch.isOn(player);

        ServerPlayer respawned =
                helper.getLevel().getServer().getPlayerList().respawn(player, false, Entity.RemovalReason.KILLED);

        helper.assertValueEqual(PathlighterSwitch.isOn(respawned), stateBeforeDeath, "switch after respawn");
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void is_on_follows_default_setting_when_player_never_toggled(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        boolean onByDefault = PathlighterSwitch.isOn(player);

        try (GameTestConfig.ConfigOverride changedDefault =
                GameTestConfig.override(Config.ENABLED_BY_DEFAULT, !onByDefault)) {
            helper.assertValueEqual(PathlighterSwitch.isOn(player), !onByDefault, "switch after default changed");
        }
        helper.succeed();
    }

    @GameTest(template = GameTestTemplates.EMPTY)
    public static void is_on_keeps_players_choice_when_default_setting_changes(GameTestHelper helper) {
        GameTestConfig.useDefaults();
        ServerPlayer player = GameTestPlayers.joinPlayer(helper);
        GameTestCommands.runToggle(helper, player.createCommandSourceStack());
        boolean chosen = PathlighterSwitch.isOn(player);

        try (GameTestConfig.ConfigOverride changedDefault =
                GameTestConfig.override(Config.ENABLED_BY_DEFAULT, !chosen)) {
            helper.assertValueEqual(PathlighterSwitch.isOn(player), chosen, "switch after default changed");
        }
        helper.succeed();
    }
}
