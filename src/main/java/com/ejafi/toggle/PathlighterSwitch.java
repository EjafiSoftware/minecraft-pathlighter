package com.ejafi.toggle;

import com.ejafi.Config;
import com.ejafi.Pathlighter;
import com.mojang.brigadier.Command;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class PathlighterSwitch {
    private static final String PROTOCOL_VERSION = "1";
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Pathlighter.MOD_ID);
    private static final Supplier<AttachmentType<Boolean>> SWITCHED_ON = ATTACHMENT_TYPES.register(
            "switched_on",
            () -> AttachmentType.builder(Config.ENABLED_BY_DEFAULT)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build());

    private PathlighterSwitch() {}

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(PathlighterSwitch::registerPayloads);
        NeoForge.EVENT_BUS.addListener(PathlighterSwitch::registerCommands);
    }

    public static boolean isOn(ServerPlayer player) {
        return player.getExistingData(SWITCHED_ON).orElseGet(Config.ENABLED_BY_DEFAULT);
    }

    private static void toggle(ServerPlayer player) {
        boolean switchedOn = !isOn(player);
        player.setData(SWITCHED_ON, switchedOn);
        player.displayClientMessage(ToggleText.announce(switchedOn), true);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .optional()
                .playToServer(TogglePayload.TYPE, TogglePayload.STREAM_CODEC, PathlighterSwitch::handleToggleRequest);
    }

    private static void handleToggleRequest(TogglePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            toggle(player);
        }
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher()
                .register(Commands.literal(Pathlighter.MOD_ID)
                        .then(Commands.literal("toggle").executes(context -> {
                            toggle(context.getSource().getPlayerOrException());
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}
