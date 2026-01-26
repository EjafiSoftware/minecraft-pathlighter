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
            } else if (fuelSlot == -1 && (stack.is(Items.COAL) || stack.is(Items.CHARCOAL))) {
                fuelSlot = i;
                fuelCount = stack.getCount();
            } else if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
                fuelCount += stack.getCount();
            } else if (stack.is(Items.SOUL_SAND) || stack.is(Items.SOUL_SOIL)) {
                // We'll consider these for soul torches if we want, but let's stick to basics for now
                // Actually, soul torches are crafted from 1 coal/charcoal, 1 stick, AND 1 soul sand/soil.
                // It's a bit more complex.
                // Let's just keep standard torches for auto-crafting for now to avoid complexity,
                // or just allow soul torches if the player has the materials.
                continue;
            } else if (stickSlot == -1 && stack.is(Items.STICK)) {
                stickSlot = i;
                stickCount = stack.getCount();
            } else if (stack.is(Items.STICK)) {
                stickCount += stack.getCount();
            }
        }

        if (totalTorches >= Config.MAX_TORCHES_LIMIT.get()) return false;
        if (fuelSlot == -1 || fuelCount <= Config.MAX_COAL_USE.get()) return false;
        if (stickSlot == -1 || stickCount <= Config.MAX_STICK_USE.get()) return false;

        if (!player.getAbilities().instabuild) {
            ItemStack fuelStack = inventory.getItem(fuelSlot);
            fuelStack.shrink(1);
            ItemStack stickStack = inventory.getItem(stickSlot);
            stickStack.shrink(1);
        }
        
        ItemStack torches = new ItemStack(Items.TORCH, 4);
        if (!inventory.add(torches)) {
            player.drop(torches, false);
        }
        
        return true;
    }
}
