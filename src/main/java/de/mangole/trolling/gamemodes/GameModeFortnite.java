package de.mangole.trolling.gamemodes;

import de.mangole.trolling.GameMode;
import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ReviveBeacon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class GameModeFortnite extends GameModeBase {

    public static ArrayList<ReviveBeacon> reviveBeacons = new ArrayList<>();
    private final FortniteSpectator fortniteSpectator;

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
            fortniteSpectator.resetFortniteSpectator(player);
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        } else {
            fortniteSpectator.resetFortniteSpectator(player);
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        }

    }

    @GameModeEvent
    public void onSpectatorInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack clicked = event.getItem();

        if (event.getHand() != EquipmentSlot.HAND) return;

        if (clicked != null && clicked.equals(fortniteSpectator.getSpectatingCompass())) {
            event.setCancelled(true);
            fortniteSpectator.getSpectatingInventory(player).open(player);
        } else {
            if (fortniteSpectator.getSpectators().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @GameModeEvent
    public void onSpectatorInventory(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof Player spectator)) return;
        if (fortniteSpectator.getSpectators().contains(spectator)) {
            event.setCancelled(true);
        }
    }

    @GameModeEvent
    public void onSpectatorMoveOutOfDistance(PlayerMoveEvent event) {
        Player spectator = event.getPlayer();
        if (!fortniteSpectator.getSpectators().contains(spectator)) return;

        Player target = fortniteSpectator.getSpectatorTargets().get(spectator);
        Location to = event.getTo();

        if (target == null) {
            ReviveBeacon beacon = getReviveBeacon(spectator);
            Location beaconLocation = beacon.getReviveLocation();
            if (to.distance(beaconLocation) > 100) {
                spectator.teleport(beaconLocation);
            }
        } else {
            if (to.distance(target.getLocation()) > 100) {
                spectator.teleport(target);
            }
        }
    }

    @GameModeEvent
    public void onTargetMoveOutOfDistance(PlayerMoveEvent event) {
        Player target = event.getPlayer();
        if (fortniteSpectator.getSpectators().contains(target)) return;

        Map<Player, Player> spectatorTargets = fortniteSpectator.getSpectatorTargets();

        if (fortniteSpectator.getSpectatorTargets().containsValue(target)) {
            spectatorTargets.forEach((spectator, target2) -> {
                if (!target.equals(target2)) {
                    if (spectator.getLocation().distance(target2.getLocation()) > 100) {
                        spectator.teleport(target2);
                    }
                }
            });
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
