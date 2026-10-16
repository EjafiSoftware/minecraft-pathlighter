package com.ejafi;

import com.ejafi.placement.TorchPlacementHandler;
import com.ejafi.preference.PlayerPreferences;
import com.ejafi.toggle.PathlighterSwitch;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Pathlighter.MOD_ID)
public final class Pathlighter {
    public static final String MOD_ID = "pathlighter";

    public Pathlighter(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        TorchPlacementHandler.register(modBus);
        PathlighterSwitch.register(modBus);
        PlayerPreferences.register(modBus);
    }
}
