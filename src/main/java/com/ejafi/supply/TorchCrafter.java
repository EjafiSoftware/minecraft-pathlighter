package com.ejafi.supply;

import java.util.Set;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class TorchCrafter {
    static final int TORCHES_PER_CRAFT = 4;
    static final Set<Item> TORCHES = Set.of(Items.TORCH, Items.SOUL_TORCH);
    private static final Set<Item> FUELS = Set.of(Items.COAL, Items.CHARCOAL);
    private static final Set<Item> STICKS = Set.of(Items.STICK);

    private final CraftingRules rules;

    TorchCrafter(CraftingRules rules) {
        this.rules = rules;
    }

    boolean canCraft(Container inventory) {
        return rules.autoCraft()
                && countItems(inventory, FUELS) > rules.coalReserve()
                && countItems(inventory, STICKS) > rules.stickReserve()
                && measureTorchRoom(inventory) >= TORCHES_PER_CRAFT;
    }

    void craftBatch(Container inventory) {
        if (!canCraft(inventory)) {
            return;
        }

        removeOne(inventory, FUELS);
        removeOne(inventory, STICKS);
        storeTorches(inventory);
    }

    private static int countItems(Container inventory, Set<Item> items) {
        return CarriedSlots.listCarriedSlots(inventory)
                .mapToObj(inventory::getItem)
                .filter(stack -> items.contains(stack.getItem()))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private static int measureTorchRoom(Container inventory) {
        return CarriedSlots.listStorageSlots(inventory)
                .map(slot -> {
                    ItemStack stack = inventory.getItem(slot);
                    if (stack.isEmpty()) {
                        return Items.TORCH.getDefaultMaxStackSize();
                    }
                    return stack.is(Items.TORCH) ? stack.getMaxStackSize() - stack.getCount() : 0;
                })
                .sum();
    }

    private static void removeOne(Container inventory, Set<Item> items) {
        int slot = CarriedSlots.listCarriedSlots(inventory)
                .filter(candidate -> items.contains(inventory.getItem(candidate).getItem()))
                .findFirst()
                .orElseThrow();
        inventory.removeItem(slot, 1);
    }

    private static void storeTorches(Container inventory) {
        int remaining = TORCHES_PER_CRAFT;
        for (int slot : CarriedSlots.listStorageSlots(inventory).toArray()) {
            ItemStack stack = inventory.getItem(slot);
            if (remaining > 0 && stack.is(Items.TORCH)) {
                int added = Math.min(remaining, stack.getMaxStackSize() - stack.getCount());
                stack.grow(added);
                remaining -= added;
            }
        }
        for (int slot : CarriedSlots.listStorageSlots(inventory).toArray()) {
            if (remaining > 0 && inventory.getItem(slot).isEmpty()) {
                int added = Math.min(remaining, Items.TORCH.getDefaultMaxStackSize());
                inventory.setItem(slot, new ItemStack(Items.TORCH, added));
                remaining -= added;
            }
        }
        inventory.setChanged();
    }
}
