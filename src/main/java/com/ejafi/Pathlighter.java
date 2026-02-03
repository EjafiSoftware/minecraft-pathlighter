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

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

@Mod(Pathlighter.MODID)
public class Pathlighter {
    public static final String MODID = "pathlighter";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    public static final Supplier<AttachmentType<TorchData>> TORCH_DATA = ATTACHMENT_TYPES.register("torch_data", () -> AttachmentType.builder(() -> new TorchData()).build());

    public Pathlighter(IEventBus modEventBus, ModContainer modContainer) {
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(new PathlighterHandler());
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pathlighter")
                .then(Commands.literal("toggle")
                        .executes(context -> {
                            boolean newValue = !Config.ENABLED.get();
                            Config.ENABLED.set(newValue);
                            Config.ENABLED.save();
                            context.getSource().sendSuccess(() -> Component.translatable(newValue ? "message.pathlighter.enabled" : "message.pathlighter.disabled"), true);
                            return 1;
                        })
                )
        );
    }
}
