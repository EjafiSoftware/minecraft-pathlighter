package com.ejafi;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = AutoTorch.MODID, dist = Dist.CLIENT)
public class AutoTorchClient {
    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.autotorch.toggle",
            GLFW.GLFW_KEY_V,
            "key.autotorch.category"
    );

    public AutoTorchClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onKeyInput);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    private void onKeyInput(InputEvent.Key event) {
        if (TOGGLE_KEY.consumeClick()) {
            boolean newState = !Config.ENABLED.get();
            Config.ENABLED.set(newState);
            Config.ENABLED.save();

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String messageKey = newState ? "message.autotorch.enabled" : "message.autotorch.disabled";
                mc.player.displayClientMessage(Component.translatable(messageKey), true);
            }
        }
    }
}
