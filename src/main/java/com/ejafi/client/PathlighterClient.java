package com.ejafi.client;

import com.ejafi.Config;
import com.ejafi.Pathlighter;
import com.ejafi.preference.client.PreferenceSync;
import com.ejafi.toggle.client.ToggleKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Pathlighter.MOD_ID, dist = Dist.CLIENT)
public final class PathlighterClient {
    public PathlighterClient(ModContainer container, IEventBus modBus) {
        container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        ToggleKey.register(modBus);
        PreferenceSync.register(modBus);
    }
}
