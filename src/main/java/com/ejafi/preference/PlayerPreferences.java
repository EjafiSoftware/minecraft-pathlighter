package com.ejafi.preference;

import com.ejafi.Config;
import com.ejafi.Pathlighter;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class PlayerPreferences {
    private static final String PROTOCOL_VERSION = "1";
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Pathlighter.MOD_ID);
    private static final Supplier<AttachmentType<Preferences>> CHOSEN = ATTACHMENT_TYPES.register(
            "preferences",
            () -> AttachmentType.builder(Config.PLAYER_DEFAULTS::read)
                    .serialize(Preferences.CODEC)
                    .copyOnDeath()
                    .build());

    private PlayerPreferences() {}

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(PlayerPreferences::registerPayloads);
    }

    public static Preferences resolve(ServerPlayer player) {
        Preferences chosen = player.getExistingData(CHOSEN).orElseGet(Config.PLAYER_DEFAULTS::read);
        return PreferenceLimiter.limit(chosen, readLimits());
    }

    private static PreferenceLimits readLimits() {
        return new PreferenceLimits(
                Config.ALLOW_AUTO_CRAFT.get(),
                Config.MINIMUM_TORCH_SPACING.get(),
                PreferenceValues.parseBlockIds(Config.ENFORCED_GROUND_BLACKLIST.get()));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .optional()
                .playToServer(
                        PreferencesPayload.TYPE, PreferencesPayload.STREAM_CODEC, PlayerPreferences::handlePreferences);
    }

    private static void handlePreferences(PreferencesPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            player.setData(CHOSEN, payload.preferences());
        }
    }
}
