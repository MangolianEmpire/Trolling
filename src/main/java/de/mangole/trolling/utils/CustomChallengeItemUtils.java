package de.mangole.trolling.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomChallengeItemUtils {

    private CustomChallengeItemUtils() {}

    public static CustomChallengeItem createCustomChallengeItem(Material material, Component name, Component description, CustomChallenge challenge) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.lore(List.of(description));
        item.setItemMeta(meta);
        return new CustomChallengeItem(item, challenge);
    }
}
