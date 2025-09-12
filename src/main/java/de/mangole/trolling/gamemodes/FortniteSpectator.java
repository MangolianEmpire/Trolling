package de.mangole.trolling.gamemodes;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomInventory;
import de.mangole.trolling.utils.CustomInventoryItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FortniteSpectator {

    private final Trolling trolling;
    private final ItemStack spectatingCompass;
    private List<Player> spectators = new ArrayList<>();
    private Map<Player, Player> spectatorTargets = new HashMap<>();

    public FortniteSpectator(Trolling trolling) {
        this.trolling = trolling;
        spectatingCompass = loadSpectatingCompass();
    }

    public void setFortniteSpectator(Player player) {
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.setHealth(20);
        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlight(true);
        player.setInvisible(true);
        player.setCollidable(false);
        player.getInventory().setItem(0, spectatingCompass);
        player.getInventory().setHelmet(new ItemStack(Material.SKELETON_SKULL));
        spectators.add(player);
        spectatorTargets.put(player, null);
    }

    public void resetFortniteSpectator(Player player) {
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.setHealth(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setInvisible(false);
        player.setAllowFlight(false);
        player.setCollidable(true);
        spectators.remove(player);
        spectatorTargets.remove(player);
    }

    public CustomInventory getSpectatingInventory(Player spectatingPlayer) {
        CustomInventory spectatorInventory = new CustomInventory(trolling, 4, Component.text("Fortnite Spectator"));
        int slot = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(spectatingPlayer)) continue;
            if (player.getGameMode() != GameMode.SURVIVAL) continue;

            spectatorInventory.setItem(slot, getPlayerHead(player));
            slot++;
        }

        return spectatorInventory;
    }

    private ItemStack loadSpectatingCompass() {
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

        return new CustomInventoryItem(head, spectator -> spectatePlayer(spectator, player));
    }

    private void spectatePlayer(Player spectator, Player target) {
        spectator.teleport(target.getLocation());
        spectator.sendMessage(Component.text("Spectating " + target.getName(), NamedTextColor.DARK_GREEN));
        spectator.playSound(spectator.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        spectatorTargets.put(spectator, target);
    }


    public List<Player> getSpectators() {
        return spectators;
    }

    public ItemStack getSpectatingCompass() {
        return spectatingCompass;
    }

    public Map<Player, Player> getSpectatorTargets() {
        return spectatorTargets;
    }
}
