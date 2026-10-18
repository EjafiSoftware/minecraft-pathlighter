package com.ejafi.placement;

import java.util.List;
import net.datafaker.Faker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

final class WalkerFaker {
    private static final Faker FAKER = new Faker();
    private static final List<Item> PICKAXES = List.of(
            Items.WOODEN_PICKAXE,
            Items.STONE_PICKAXE,
            Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE,
            Items.DIAMOND_PICKAXE,
            Items.NETHERITE_PICKAXE);

    private boolean onFoot = true;
    private boolean sneaking;
    private boolean mayBuild = true;
    private ItemStack mainHand = new ItemStack(FAKER.options().nextElement(PICKAXES));
    private BlockPos position = BlockPosFaker.generate();
    private ResourceKey<Level> dimension = DimensionFaker.generate();
    private @Nullable TorchMark lastTorch;

    WalkerFaker withoutFooting() {
        this.onFoot = false;
        return this;
    }

    WalkerFaker withSneaking() {
        this.sneaking = true;
        return this;
    }

    WalkerFaker withoutBuildPermission() {
        this.mayBuild = false;
        return this;
    }

    WalkerFaker withMainHand(ItemStack mainHand) {
        this.mainHand = mainHand;
        return this;
    }

    WalkerFaker withPosition(BlockPos position) {
        this.position = position;
        return this;
    }

    WalkerFaker withDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
        return this;
    }

    WalkerFaker withLastTorch(@Nullable TorchMark lastTorch) {
        this.lastTorch = lastTorch;
        return this;
    }

    Walker generate() {
        return new Walker(onFoot, sneaking, mayBuild, mainHand, position, dimension, lastTorch);
    }
}
