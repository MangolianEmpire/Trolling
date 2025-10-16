package de.mangole.trolling.customMobs;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface CustomLootDroppable {

    List<ItemStack> drops = new ArrayList<>();

    List<ItemStack> getDrops();
    void setDrops(List<ItemStack> drops);
}
