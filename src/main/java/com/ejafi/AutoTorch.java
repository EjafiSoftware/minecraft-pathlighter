package com.ejafi;

import com.ejafi.service.TorchData;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(AutoTorch.MODID)
public class AutoTorch {
    public static final String MODID = "autotorch";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    public static final Supplier<AttachmentType<TorchData>> TORCH_DATA = ATTACHMENT_TYPES.register("torch_data", () -> AttachmentType.builder(() -> new TorchData()).build());

    public AutoTorch(IEventBus modEventBus, ModContainer modContainer) {
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(new AutoTorchHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
