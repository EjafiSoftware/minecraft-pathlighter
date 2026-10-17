package com.ejafi;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTestHelper;

public final class GameTestCommands {
    private static final String TOGGLE_COMMAND = "pathlighter toggle";

    private GameTestCommands() {}

    public static void runToggle(GameTestHelper helper, CommandSourceStack source) {
        helper.getLevel().getServer().getCommands().performPrefixedCommand(source, TOGGLE_COMMAND);
    }
}
