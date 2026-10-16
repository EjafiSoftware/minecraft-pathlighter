package com.ejafi.placement;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.datafaker.Faker;
import net.minecraft.resources.ResourceLocation;

public final class BlockIdFaker {
    private static final Faker FAKER = new Faker();
    private static final int MAX_SET_SIZE = 3;

    private BlockIdFaker() {}

    public static ResourceLocation generate() {
        return ResourceLocation.fromNamespaceAndPath(
                FAKER.lorem().word().toLowerCase(Locale.ROOT), FAKER.internet().uuid());
    }

    public static Set<ResourceLocation> generateSet() {
        return generateSetOfAtLeast(0);
    }

    public static Set<ResourceLocation> generateNonEmptySet() {
        return generateSetOfAtLeast(1);
    }

    private static Set<ResourceLocation> generateSetOfAtLeast(int minimumSize) {
        return Stream.generate(BlockIdFaker::generate)
                .limit(FAKER.number().numberBetween(minimumSize, MAX_SET_SIZE + 1))
                .collect(Collectors.toUnmodifiableSet());
    }
}
