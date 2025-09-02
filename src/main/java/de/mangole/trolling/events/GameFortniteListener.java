package de.mangole.trolling.events;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.GameMode;
import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ReviveBeacon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class GameFortniteListener implements Listener {

    private final Trolling trolling;
    public static ArrayList<ReviveBeacon> reviveBeacons = new ArrayList<>();

    public GameFortniteListener(Trolling trolling) {
        this.trolling = trolling;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (GameManager.gameMode == GameMode.FORTNITE) {
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
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (GameManager.gameMode == GameMode.FORTNITE) {
            Player player = event.getPlayer();

            if (hasReviveBeacon(player)) {
                player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                player.teleport(getReviveBeacon(player).getReviveLocation());
                Player nearest = getNearestAlivePlayer(player);
                if (nearest != null) {
                    player.setSpectatorTarget(nearest);
                }
            } else if (GameManager.gameStatus == GameStatus.LOST){
                player.setGameMode(org.bukkit.GameMode.SPECTATOR);
            } else {
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathSpectatorTarget(PlayerDeathEvent event) {
        Player dead = event.getEntity();

        // alle Spieler prüfen, die gerade als Spectator ein Target haben
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.getGameMode() != org.bukkit.GameMode.SPECTATOR) continue;
            if (viewer.getSpectatorTarget() == null) continue;

            // schaut der Zuschauer gerade auf den toten Spieler?
            if (viewer.getSpectatorTarget().equals(dead)) {
                Player next = getNearestAlivePlayer(viewer);
                if (next != null) {
                    viewer.setSpectatorTarget(next); // neues Ziel
                } else {
                    viewer.setSpectatorTarget(null); // frei fliegen lassen
                }
            }
        }
    }


    private Player getNearestAlivePlayer(Player deadPlayer) {
        double nearestDistance = Double.MAX_VALUE;
        Player nearest = null;
        Location loc = deadPlayer.getLocation();

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(deadPlayer)) continue;
            if (other.getGameMode() != org.bukkit.GameMode.SURVIVAL) continue; // nur lebende Spieler

            double dist = other.getLocation().distanceSquared(loc);
            if (dist < nearestDistance) {
                nearestDistance = dist;
                nearest = other;
            }
        }
        return nearest;
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
