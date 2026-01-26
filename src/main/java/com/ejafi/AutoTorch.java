package com.ejafi;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AutoTorch.MODID)
public class AutoTorch {
    public static final String MODID = "autotorch";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AutoTorch(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(new AutoTorchHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
