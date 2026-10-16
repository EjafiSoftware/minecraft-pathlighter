package com.ejafi.placement;

import net.minecraft.resources.ResourceLocation;

final class SpotFaker {
    private static final LightSample DARKNESS = new LightSample(0, 0, 0);

    private LightSample light = DARKNESS;
    private ResourceLocation ground = BlockIdFaker.generate();
    private boolean torchFits = true;
    private boolean buildable = true;

    SpotFaker withLight(LightSample light) {
        this.light = light;
        return this;
    }

    SpotFaker withGround(ResourceLocation ground) {
        this.ground = ground;
        return this;
    }

    SpotFaker withoutRoomForTorch() {
        this.torchFits = false;
        return this;
    }

    SpotFaker withProtection() {
        this.buildable = false;
        return this;
    }

    Spot generate() {
        return new Spot(light, ground, torchFits, buildable);
    }
}
