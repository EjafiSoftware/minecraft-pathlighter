package com.ejafi.supply;

import java.util.ArrayList;
import java.util.List;
import net.datafaker.Faker;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Builds a container laid out like a player's inventory: main slots, then armor, then the offhand. */
final class InventoryFaker {
    private static final Faker FAKER = new Faker();
    private static final int PLAYER_SLOTS = Inventory.SLOT_OFFHAND + 1;
    private static final List<Item> JUNK =
            List.of(Items.COBBLESTONE, Items.DIRT, Items.GRAVEL, Items.ANDESITE, Items.DIORITE, Items.GRANITE);

    private final List<ItemStack> stacks = new ArrayList<>();
    private ItemStack offhand = ItemStack.EMPTY;
    private boolean junkInFreeSlots;

    InventoryFaker withItems(Item item, int count) {
        int maxStackSize = new ItemStack(item).getMaxStackSize();
        for (int remaining = count; remaining > 0; remaining -= maxStackSize) {
            stacks.add(new ItemStack(item, Math.min(remaining, maxStackSize)));
        }
        return this;
    }

    InventoryFaker withOffhand(Item item, int count) {
        this.offhand = new ItemStack(item, count);
        return this;
    }

    InventoryFaker withJunkInFreeSlots() {
        this.junkInFreeSlots = true;
        return this;
    }

    Container generate() {
        SimpleContainer inventory = new SimpleContainer(PLAYER_SLOTS);
        for (int slot = 0; slot < stacks.size(); slot++) {
            inventory.setItem(slot, stacks.get(slot).copy());
        }
        if (junkInFreeSlots) {
            for (int slot = stacks.size(); slot < Inventory.INVENTORY_SIZE; slot++) {
                ItemStack junk = new ItemStack(FAKER.options().nextElement(JUNK));
                junk.setCount(junk.getMaxStackSize());
                inventory.setItem(slot, junk);
            }
        }
        inventory.setItem(Inventory.SLOT_OFFHAND, offhand.copy());
        return inventory;
    }
}
