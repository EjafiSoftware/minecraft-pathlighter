package com.ejafi.supply;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class TorchSupply {
    private final TorchCrafter crafter;

    public TorchSupply(CraftingRules rules) {
        this.crafter = new TorchCrafter(rules);
    }

    public Optional<Block> findTorch(Container inventory) {
        OptionalInt slot = findTorchSlot(inventory);
        if (slot.isPresent()) {
            return Optional.of(toTorchBlock(inventory.getItem(slot.getAsInt())));
        }

        return crafter.canCraft(inventory) ? Optional.of(Blocks.TORCH) : Optional.empty();
    }

    public Optional<Block> takeTorch(Container inventory) {
        if (findTorchSlot(inventory).isEmpty()) {
            crafter.craftBatch(inventory);
        }

        OptionalInt slot = findTorchSlot(inventory);
        if (slot.isEmpty()) {
            return Optional.empty();
        }

        Block torch = toTorchBlock(inventory.getItem(slot.getAsInt()));
        inventory.removeItem(slot.getAsInt(), 1);
        return Optional.of(torch);
    }

    private static OptionalInt findTorchSlot(Container inventory) {
        return CarriedSlots.listCarriedSlots(inventory)
                .filter(slot ->
                        TorchCrafter.TORCHES.contains(inventory.getItem(slot).getItem()))
                .findFirst();
    }

    private static Block toTorchBlock(ItemStack torch) {
        return torch.is(Items.SOUL_TORCH) ? Blocks.SOUL_TORCH : Blocks.TORCH;
    }
}
