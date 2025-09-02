package de.mangole.trolling.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class CustomInventory  implements InventoryHolder {

    private final Inventory inventory;
    private final Map<Integer, CustomInventoryItem> itemMap = new HashMap<>();

    public CustomInventory(Plugin plugin, int rows, Component title) {
        this.inventory = plugin.getServer().createInventory(this, rows * 9, title);
    }

    public void setItem(int slot, CustomInventoryItem item) {
        inventory.setItem(slot, item.getItem());
        itemMap.put(slot, item);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleClick(int slot, Player player) {
        if (itemMap.containsKey(slot)) {
            itemMap.get(slot).click(player);
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
