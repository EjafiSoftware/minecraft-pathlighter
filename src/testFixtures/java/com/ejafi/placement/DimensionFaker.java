package com.ejafi.placement;

import java.util.List;
import net.datafaker.Faker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

final class DimensionFaker {
    private static final Faker FAKER = new Faker();
    private static final List<ResourceKey<Level>> DIMENSIONS = List.of(Level.OVERWORLD, Level.NETHER, Level.END);

    private DimensionFaker() {}

    static ResourceKey<Level> generate() {
        return FAKER.options().nextElement(DIMENSIONS);
    }

    static ResourceKey<Level> generateOtherThan(ResourceKey<Level> excluded) {
        return FAKER.options()
                .nextElement(DIMENSIONS.stream()
                        .filter(dimension -> !dimension.equals(excluded))
                        .toList());
    }
}
