package com.ejafi.supply;

import static com.ejafi.supply.TorchCrafter.TORCHES_PER_CRAFT;
import static org.assertj.core.api.Assertions.assertThat;

import net.datafaker.Faker;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

class TorchCrafterTest {
    private static final Faker FAKER = new Faker();
    private static final int FULL_STACK = Items.TORCH.getDefaultMaxStackSize();
    private static final int MAX_RESERVE = 16;

    @Test
    void craftBatch_craftsFourTorchesFromOneFuelAndOneStick_whenCarryingIngredients() {
        int coal = FAKER.number().numberBetween(1, FULL_STACK + 1);
        int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, coal)
                .withItems(Items.STICK, sticks)
                .generate();
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(TORCHES_PER_CRAFT, 0, coal - 1, 0, sticks - 1));
    }

    @Test
    void craftBatch_usesCharcoal_whenCarryingNoCoal() {
        int charcoal = FAKER.number().numberBetween(1, FULL_STACK + 1);
        int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.CHARCOAL, charcoal)
                .withItems(Items.STICK, sticks)
                .generate();
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(TORCHES_PER_CRAFT, 0, 0, charcoal - 1, sticks - 1));
    }

    @Test
    void craftBatch_consumesOffhandFuel_whenOnlyFuelIsInOffhand() {
        int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withOffhand(Items.COAL, 1)
                .withItems(Items.STICK, sticks)
                .generate();
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(TORCHES_PER_CRAFT, 0, 0, 0, sticks - 1));
    }

    @Test
    void craftBatch_consumesOffhandSticks_whenOnlySticksAreInOffhand() {
        int coal = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, coal)
                .withOffhand(Items.STICK, 1)
                .generate();
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(TORCHES_PER_CRAFT, 0, coal - 1, 0, 0));
    }

    @Test
    void craftBatch_craftsBatch_whenFuelExceedsCoalReserve() {
        int reserve = FAKER.number().numberBetween(1, MAX_RESERVE + 1);
        int sticks = FAKER.number().numberBetween(1, FULL_STACK + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, reserve + 1)
                .withItems(Items.STICK, sticks)
                .generate();
        CraftingRules rules = new CraftingRulesFaker()
                .withCoalReserve(reserve)
                .withStickReserve(0)
                .generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(new Stock(TORCHES_PER_CRAFT, 0, reserve, 0, sticks - 1));
    }

    @Test
    void craftBatch_craftsNothing_whenFuelIsAtCoalReserve() {
        int reserve = FAKER.number().numberBetween(0, MAX_RESERVE + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, reserve)
                .withItems(Items.STICK, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        Stock before = Stock.takeStock(inventory);
        CraftingRules rules = new CraftingRulesFaker()
                .withCoalReserve(reserve)
                .withStickReserve(0)
                .generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void craftBatch_countsCharcoalTowardCoalReserve_whenCarryingBoth() {
        int reserve = FAKER.number().numberBetween(1, MAX_RESERVE + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, reserve)
                .withItems(Items.CHARCOAL, 1)
                .withItems(Items.STICK, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        CraftingRules rules = new CraftingRulesFaker()
                .withCoalReserve(reserve)
                .withStickReserve(0)
                .generate();

        new TorchCrafter(rules).craftBatch(inventory);

        Stock stock = Stock.takeStock(inventory);
        assertThat(stock.torches()).isEqualTo(TORCHES_PER_CRAFT);
        assertThat(stock.coal() + stock.charcoal()).isEqualTo(reserve);
    }

    @Test
    void craftBatch_craftsNothing_whenSticksAreAtStickReserve() {
        int reserve = FAKER.number().numberBetween(0, MAX_RESERVE + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .withItems(Items.STICK, reserve)
                .generate();
        Stock before = Stock.takeStock(inventory);
        CraftingRules rules = new CraftingRulesFaker()
                .withCoalReserve(0)
                .withStickReserve(reserve)
                .generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void craftBatch_craftsNothing_whenAutoCraftIsOff() {
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .withItems(Items.STICK, FAKER.number().numberBetween(1, FULL_STACK + 1))
                .generate();
        Stock before = Stock.takeStock(inventory);
        CraftingRules rules =
                new CraftingRulesFaker().withoutAutoCraft().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void craftBatch_craftsNothing_whenInventoryHasNoRoomForTorches() {
        Container inventory = new InventoryFaker()
                .withItems(Items.COAL, FULL_STACK)
                .withItems(Items.STICK, FULL_STACK)
                .withJunkInFreeSlots()
                .generate();
        Stock before = Stock.takeStock(inventory);
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void craftBatch_craftsNothing_whenFewerThanFourTorchesFit() {
        Container inventory = new InventoryFaker()
                .withItems(Items.TORCH, FULL_STACK - FAKER.number().numberBetween(1, TORCHES_PER_CRAFT))
                .withItems(Items.COAL, FULL_STACK)
                .withItems(Items.STICK, FULL_STACK)
                .withJunkInFreeSlots()
                .generate();
        Stock before = Stock.takeStock(inventory);
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory)).isEqualTo(before);
    }

    @Test
    void craftBatch_topsUpCarriedTorchStack_whenOnlyThatStackHasRoom() {
        int carried = FAKER.number().numberBetween(1, FULL_STACK - TORCHES_PER_CRAFT + 1);
        Container inventory = new InventoryFaker()
                .withItems(Items.TORCH, carried)
                .withItems(Items.COAL, FULL_STACK)
                .withItems(Items.STICK, FULL_STACK)
                .withJunkInFreeSlots()
                .generate();
        CraftingRules rules = new CraftingRulesFaker().withoutReserves().generate();

        new TorchCrafter(rules).craftBatch(inventory);

        assertThat(Stock.takeStock(inventory))
                .isEqualTo(new Stock(carried + TORCHES_PER_CRAFT, 0, FULL_STACK - 1, 0, FULL_STACK - 1));
    }
}
