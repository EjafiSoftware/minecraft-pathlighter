package com.ejafi.supply;

import net.minecraft.world.Container;
import net.minecraft.world.item.Items;

record Stock(int torches, int soulTorches, int coal, int charcoal, int sticks) {
    static Stock takeStock(Container container) {
        return new Stock(
                container.countItem(Items.TORCH),
                container.countItem(Items.SOUL_TORCH),
                container.countItem(Items.COAL),
                container.countItem(Items.CHARCOAL),
                container.countItem(Items.STICK));
    }
}
