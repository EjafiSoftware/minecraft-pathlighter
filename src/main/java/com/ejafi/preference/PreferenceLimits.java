package com.ejafi.preference;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;

record PreferenceLimits(
        boolean allowAutoCraft, int minimumTorchSpacing, Set<ResourceLocation> enforcedGroundBlacklist) {
    PreferenceLimits {
        enforcedGroundBlacklist = Set.copyOf(enforcedGroundBlacklist);
    }
}
