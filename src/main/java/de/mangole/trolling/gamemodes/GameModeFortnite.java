package de.mangole.trolling.gamemodes;

import de.mangole.trolling.GameMode;
import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ReviveBeacon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class GameModeFortnite extends GameModeBase {

    public static ArrayList<ReviveBeacon> reviveBeacons = new ArrayList<>();
    private FortniteSpectator fortniteSpectator;

    public GameModeFortnite(Trolling trolling) {
        super(trolling, GameMode.FORTNITE);
        this.fortniteSpectator = new FortniteSpectator(trolling);
    }

    @GameModeEvent
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (reviveBeacons.size() + 1 >= Bukkit.getOnlinePlayers().size()) {
            for (ReviveBeacon beacon : new ArrayList<>(reviveBeacons)) {
                beacon.cleanup();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    trolling.getGameManager().loseGame();
                }
            }.runTaskLater(trolling, 2L);

        } else {
            ReviveBeacon reviveBeacon = new ReviveBeacon(trolling, player, 5, 200, player.getLocation().getBlock());
            reviveBeacons.add(reviveBeacon);
        }
    }

    @GameModeEvent
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (hasReviveBeacon(player)) {
            fortniteSpectator.setFortniteSpectator(player);
            player.teleport(getReviveBeacon(player).getReviveLocation());
        } else if (gameManager.getGameStatus() == GameStatus.LOST) {
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        } else {
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        }

    }

    @GameModeEvent
    public void onCustomClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack clicked = e.getItem();

        if (e.getHand() != EquipmentSlot.HAND) return;

        if (clicked != null && clicked.equals(FortniteSpectator.spectatingCompass)) {
            e.setCancelled(true);
            fortniteSpectator.getSpectatingInventory(player).open(player);
        }
    }


    private boolean hasReviveBeacon(Player player) {
        for (ReviveBeacon reviveBeacon : reviveBeacons) {
            if (reviveBeacon.getRevivePlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }

    private ReviveBeacon getReviveBeacon(Player player) {
        for (ReviveBeacon reviveBeacon : reviveBeacons) {
            if (reviveBeacon.getRevivePlayer().equals(player)) {
                return reviveBeacon;
            }
        }
        return null;
    }
}
