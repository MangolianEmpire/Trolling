package de.mangole.trolling.utils;

import de.mangole.trolling.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomChallengeItem extends CustomInventoryItem {

    private final CustomChallenge challenge;

    public CustomChallengeItem(ItemStack item, CustomChallenge challenge) {
        super(setGlint(item, challenge.isActive()), player -> {
            if (challenge.isActive()) {
                challenge.deactivate();
                setGlint(item, false);
                player.closeInventory();
            } else {
                challenge.activate();
                setGlint(item, true);
                player.closeInventory();
            }
        });
        this.challenge = challenge;
    }

    private static ItemStack setGlint(ItemStack item, boolean isActive) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setEnchantmentGlintOverride(isActive);
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore = new ArrayList<>(lore); // Defensive copy
            }

            if (lore.size() > 1) {
                lore.removeLast();
            }
            lore.add(Component.text(isActive ? "[ON]" : "[OFF]", isActive ? NamedTextColor.GREEN : NamedTextColor.RED));

            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public CustomChallenge getChallenge() {
        return challenge;
    }
}
