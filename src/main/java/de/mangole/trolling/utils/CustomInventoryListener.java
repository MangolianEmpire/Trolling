package de.mangole.trolling.utils;

import de.mangole.trolling.Data;
import de.mangole.trolling.GameManager;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryListener implements Listener {

    private Trolling trolling;

    public CustomInventoryListener(Trolling t) {
        this.trolling = t;
    }

    @EventHandler
    public void onCustomInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        int slot = e.getRawSlot();
        Player player = (Player) e.getWhoClicked();
        World world = player.getWorld();

        if ((e.getInventory().getHolder() instanceof CustomInventory customInventory)) {
            e.setCancelled(true);
            customInventory.handleClick(slot, player);
        }

        ItemStack clicked = e.getCurrentItem();

        if (handleInventoryClick(clicked, player, world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCustomClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack clicked = e.getItem();
        World world = player.getWorld();

        if (e.getHand() != EquipmentSlot.HAND) return;

        if (handleInventoryClick(clicked, player, world)) {
            e.setCancelled(true);
        }
    }


    private boolean handleInventoryClick(ItemStack clicked, Player player, World world) {
        if (clicked != null && clicked.isSimilar(Data.lobbySpawnTeleporter)) {
            if (world.getName().equals("ChallengesLobby_world")) {
                player.teleport(WorldManager.lobbySpawn);
                world.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
            return true;
        }

        if (clicked != null && clicked.isSimilar(Data.lobbySettings)) {
            if (world.getName().equals("ChallengesLobby_world")) {
                Data.lobbySettingsInventory.open(player);
            }
            return true;
        }

        if (clicked != null && clicked.isSimilar(Data.lobbyGameModeChanger)) {
            if (world.getName().equals("ChallengesLobby_world")) {
                trolling.getGameManager().switchGameMode();
            }
            return true;
        }

        return false;
    }
}
