package com.ejafi.service;

import com.ejafi.Config;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CraftingService {

    public int findTorch(Inventory inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && (stack.is(Items.TORCH) || stack.is(Items.SOUL_TORCH))) {
                return i;
            }
        }
        return -1;
    }

    public boolean tryAutoCraftTorches(Player player) {
        Inventory inventory = player.getInventory();
        
        int fuelSlot = -1;
        int fuelCount = 0;
        int stickSlot = -1;
        int stickCount = 0;
        int totalTorches = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.TORCH)) {
                totalTorches += stack.getCount();
            } else if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
                if (fuelSlot == -1) fuelSlot = i;
                fuelCount += stack.getCount();
            } else if (stack.is(Items.STICK)) {
                if (stickSlot == -1) stickSlot = i;
                stickCount += stack.getCount();
            }
        }

        if (totalTorches >= Config.MAX_TORCHES_LIMIT.get()) return false;
        if (fuelSlot == -1 || fuelCount <= Config.MAX_COAL_USE.get()) return false;
        if (stickSlot == -1 || stickCount <= Config.MAX_STICK_USE.get()) return false;

        if (!player.getAbilities().instabuild) {
            inventory.removeItem(fuelSlot, 1);
            inventory.removeItem(stickSlot, 1);
        }
        
        ItemStack torches = new ItemStack(Items.TORCH, 4);
        if (!inventory.add(torches)) {
            player.drop(torches, false);
        }
        
        return true;
    }
}
