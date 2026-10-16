package com.ejafi.toggle;

import net.minecraft.network.chat.Component;

public final class ToggleText {
    public static final String SWITCHED_ON_KEY = "message.pathlighter.enabled";
    public static final String SWITCHED_ON_ENGLISH = "Pathlighter: Enabled";
    public static final String SWITCHED_OFF_KEY = "message.pathlighter.disabled";
    public static final String SWITCHED_OFF_ENGLISH = "Pathlighter: Disabled";
    public static final String UNAVAILABLE_KEY = "message.pathlighter.unavailable";
    public static final String KEY_CATEGORY = "key.pathlighter.category";
    public static final String TOGGLE_KEY = "key.pathlighter.toggle";

    private ToggleText() {}

    static Component announce(boolean switchedOn) {
        return switchedOn
                ? Component.translatableWithFallback(SWITCHED_ON_KEY, SWITCHED_ON_ENGLISH)
                : Component.translatableWithFallback(SWITCHED_OFF_KEY, SWITCHED_OFF_ENGLISH);
    }
}
