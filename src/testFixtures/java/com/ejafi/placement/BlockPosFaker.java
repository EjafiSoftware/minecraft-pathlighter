package com.ejafi.placement;

import net.datafaker.Faker;
import net.minecraft.core.BlockPos;

final class BlockPosFaker {
    private static final Faker FAKER = new Faker();
    private static final int HORIZONTAL_REACH = 100_000;
    private static final int MIN_Y = -64;
    private static final int MAX_Y = 319;

    private BlockPosFaker() {}

    static BlockPos generate() {
        return new BlockPos(
                FAKER.number().numberBetween(-HORIZONTAL_REACH, HORIZONTAL_REACH),
                FAKER.number().numberBetween(MIN_Y, MAX_Y + 1),
                FAKER.number().numberBetween(-HORIZONTAL_REACH, HORIZONTAL_REACH));
    }
}
