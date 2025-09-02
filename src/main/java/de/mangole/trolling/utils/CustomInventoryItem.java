package de.mangole.trolling.utils;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class CustomInventoryItem {

    private final ItemStack item;
    private final Consumer<Player> onClick;

    public CustomInventoryItem(ItemStack item, Consumer<Player> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    public ItemStack getItem() {
        return item;
    }

    public void click(Player player) {
        onClick.accept(player);
    }
}
