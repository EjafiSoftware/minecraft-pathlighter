package com.ejafi.supply;

import java.util.stream.IntStream;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

final class CarriedSlots {
    private CarriedSlots() {}

    static IntStream listStorageSlots(Container inventory) {
        return IntStream.range(0, Math.min(inventory.getContainerSize(), Inventory.INVENTORY_SIZE));
    }

    static IntStream listCarriedSlots(Container inventory) {
        IntStream offhand = inventory.getContainerSize() > Inventory.SLOT_OFFHAND
                ? IntStream.of(Inventory.SLOT_OFFHAND)
                : IntStream.empty();
        return IntStream.concat(listStorageSlots(inventory), offhand);
    }
}
