package com.ejafi.toggle.client;

import com.ejafi.toggle.TogglePayload;
import com.ejafi.toggle.ToggleText;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public final class ToggleKey {
    private static final KeyMapping TOGGLE =
            new KeyMapping(ToggleText.TOGGLE_KEY, GLFW.GLFW_KEY_V, ToggleText.KEY_CATEGORY);
    private static boolean heldLastTick;

    private ToggleKey() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ToggleKey::registerKeyMapping);
        NeoForge.EVENT_BUS.addListener(ToggleKey::onClientTick);
    }

    private static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        boolean clicked = false;
        while (TOGGLE.consumeClick()) {
            clicked = true;
        }
        if (clicked && !heldLastTick) {
            requestToggle();
        }
        heldLastTick = TOGGLE.isDown();
    }

    private static void requestToggle() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        LocalPlayer player = minecraft.player;
        if (connection == null || player == null) {
            return;
        }

        if (connection.hasChannel(TogglePayload.TYPE)) {
            PacketDistributor.sendToServer(new TogglePayload());
        } else {
            player.displayClientMessage(Component.translatable(ToggleText.UNAVAILABLE_KEY), true);
        }
    }
}
