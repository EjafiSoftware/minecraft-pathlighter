package com.ejafi.preference.client;

import com.ejafi.Config;
import com.ejafi.preference.PreferencesPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PreferenceSync {
    private PreferenceSync() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(PreferenceSync::onConfigReloading);
        NeoForge.EVENT_BUS.addListener(PreferenceSync::onLoggingIn);
    }

    private static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        sendPreferences();
    }

    private static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (Config.CLIENT_SPEC.equals(event.getConfig().getSpec())) {
            Minecraft.getInstance().execute(PreferenceSync::sendPreferences);
        }
    }

    private static void sendPreferences() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null && connection.hasChannel(PreferencesPayload.TYPE)) {
            PacketDistributor.sendToServer(new PreferencesPayload(Config.PREFERENCES.read()));
        }
    }
}
