package de.mangole.trolling.gamemodes;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomInventory;
import de.mangole.trolling.utils.CustomInventoryItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FortniteSpectator {

    private final Trolling trolling;
    public static ItemStack spectatingCompass;
    private List<Player> spectators = new ArrayList<>();

    public FortniteSpectator(Trolling trolling) {
        this.trolling = trolling;
        spectatingCompass = getSpectatingCompass();
    }

    public void setFortniteSpectator(Player player) {
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.setHealth(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.setCollidable(false);
        player.getInventory().setItem(0, spectatingCompass);
        spectators.add(player);
    }

    public void resetFortniteSpectator(Player player) {
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setCollidable(true);
        spectators.remove(player);
    }

    public CustomInventory getSpectatingInventory(Player spectatingPlayer) {
        CustomInventory spectatorInventory = new CustomInventory(trolling, 4, Component.text("Fortnite Spectator"));
        int slot = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(spectatingPlayer)) continue;

            spectatorInventory.setItem(slot, getPlayerHead(player));
            slot++;
        }

        return spectatorInventory;
    }

    private ItemStack getSpectatingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.displayName(Component.text("Teleporter", NamedTextColor.AQUA));
        meta.lore(List.of(Component.text("Spectate living players", NamedTextColor.AQUA)));
        compass.setItemMeta(meta);
        return compass;
    }

    private CustomInventoryItem getPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.displayName(Component.text(player.getName(), NamedTextColor.GREEN));
            meta.lore(List.of(
                    Component.text("Click to teleport to " + player.getName(), NamedTextColor.GRAY)
            ));
            head.setItemMeta(meta);
        }

        return new CustomInventoryItem(head, spectator -> {
            spectator.teleport(player.getLocation());
            spectator.sendMessage(Component.text("Spectating " + player.getName(), NamedTextColor.DARK_GREEN));
        });
    }

}
