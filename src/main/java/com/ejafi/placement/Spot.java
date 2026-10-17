package com.ejafi.placement;

import net.minecraft.resources.ResourceLocation;

record Spot(LightSample light, ResourceLocation ground, boolean torchFits, boolean buildable) {}
