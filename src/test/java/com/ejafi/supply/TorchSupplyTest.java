package com.ejafi.supply;

import static com.ejafi.supply.TorchCrafter.TORCHES_PER_CRAFT;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import net.datafaker.Faker;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

class TorchSupplyTest {
    private static final Faker FAKER = new Faker();
    private static final int FULL_STACK = Items.TORCH.getDefaultMaxStackSize();

    @Test
    void takeTorch_removesOneTorch_whenCarryingTorches() {
        int carried = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory =
                new InventoryFaker().withItems(Items.TORCH, carried).generate();
        TorchSupply supply = new TorchSupply(new CraftingRulesFaker().generate());

        Optional<Block> torch = supply.takeTorch(inventory);

        assertThat(torch).contains(Blocks.TORCH);
        assertThat(inventory.countItem(Items.TORCH)).isEqualTo(carried - 1);
    }

    @Test
    void takeTorch_returnsSoulTorch_whenCarryingOnlySoulTorches() {
        int carried = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory =
                new InventoryFaker().withItems(Items.SOUL_TORCH, carried).generate();
        TorchSupply supply = new TorchSupply(new CraftingRulesFaker().generate());

        Optional<Block> torch = supply.takeTorch(inventory);

        assertThat(torch).contains(Blocks.SOUL_TORCH);
        assertThat(inventory.countItem(Items.SOUL_TORCH)).isEqualTo(carried - 1);
    }

    @Test
    void takeTorch_takesOffhandTorch_whenOnlyOffhandHoldsTorches() {
        int carried = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory =
                new InventoryFaker().withOffhand(Items.TORCH, carried).generate();
        TorchSupply supply = new TorchSupply(new CraftingRulesFaker().generate());

        Optional<Block> torch = supply.takeTorch(inventory);

        assertThat(torch).contains(Blocks.TORCH);
        assertThat(inventory.countItem(Items.TORCH)).isEqualTo(carried - 1);
    }

    @Test
    void takeTorch_leavesIngredientsAlone_whenCarryingTorches() {
        int carried = FAKER.number().numberBetween(1, FULL_STACK + 1);
        int coal = FAKER.number().numberBetween(1, FULL_STACK + 1);
        int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.TORCH, carried)
                .withItems(Items.COAL, coal)
                .withItems(Items.STICK, sticks)
                .generate();
        TorchSupply supply =
                new TorchSupply(new CraftingRulesFaker().withoutReserves().generate());

        supply.takeTorch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(carried - 1, 0, coal, 0, sticks));
    }

    @Test
    void takeTorch_craftsOneBatchThenTakesOne_whenOutOfTorchesAndHoldingIngredients() {
        int coal = FAKER.number().numberBetween(1, FULL_STACK + 1);
        int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, coal)
                .withItems(Items.STICK, sticks)
                .generate();
        TorchSupply supply =
                new TorchSupply(new CraftingRulesFaker().withoutReserves().generate());

        Optional<Block> torch = supply.takeTorch(inventory);

        assertThat(torch).contains(Blocks.TORCH);
        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(TORCHES_PER_CRAFT - 1, 0, coal - 1, 0, sticks - 1));
    }

    @Test
    void takeTorch_returnsNothing_whenOutOfTorchesAndUnableToCraft() {
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        Stock before = Stock.takeStock(inventory);
        TorchSupply supply = new TorchSupply(new CraftingRulesFaker().generate());

        Optional<Block> torch = supply.takeTorch(inventory);

        assertThat(torch).isEmpty();
        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void takeTorch_craftsNothing_whenOutOfTorchesAndAutoCraftIsOff() {
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .withItems(Items.STICK, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        Stock before = Stock.takeStock(inventory);
        TorchSupply supply = new TorchSupply(
                new CraftingRulesFaker().withoutAutoCraft().withoutReserves().generate());

        Optional<Block> torch = supply.takeTorch(inventory);

        assertThat(torch).isEmpty();
        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void findTorch_leavesTorchInInventory_whenCarryingTorches() {
        int carried = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory =
                new InventoryFaker().withItems(Items.TORCH, carried).generate();
        TorchSupply supply = new TorchSupply(new CraftingRulesFaker().generate());

        Optional<Block> torch = supply.findTorch(inventory);

        assertThat(torch).contains(Blocks.TORCH);
        assertThat(inventory.countItem(Items.TORCH)).isEqualTo(carried);
    }

    @Test
    void findTorch_offersTorchWithoutCrafting_whenOutOfTorchesAndAbleToCraft() {
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .withItems(Items.STICK, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        Stock before = Stock.takeStock(inventory);
        TorchSupply supply =
                new TorchSupply(new CraftingRulesFaker().withoutReserves().generate());

        Optional<Block> torch = supply.findTorch(inventory);

        assertThat(torch).contains(Blocks.TORCH);
        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void findTorch_returnsNothing_whenOutOfTorchesAndUnableToCraft() {
        Container inventory = new InventoryFaker()
                .withItems(Items.STICK, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        TorchSupply supply = new TorchSupply(new CraftingRulesFaker().generate());

        Optional<Block> torch = supply.findTorch(inventory);

        assertThat(torch).isEmpty();
    }
}
