package de.mangole.trolling.events;

import de.mangole.trolling.*;
import de.mangole.trolling.utils.CustomChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyListener implements Listener {

    private final Trolling trolling;
    private boolean isCountdown = false;
    private final GameManager gameManager;

    public LobbyListener(final Trolling trolling) {
        this.trolling = trolling;
        this.gameManager = trolling.getGameManager();
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        World world = event.getBlock().getWorld();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockPlaceEvent event) {
        World world = event.getBlock().getWorld();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        World world = event.getEntity().getWorld();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        World world = event.getEntity().getWorld();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(FoodLevelChangeEvent event) {
        World world = event.getEntity().getWorld();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        World world = event.getPlayer().getWorld();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNetherPortal(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;

        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();
        if (world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
            event.setCancelled(true);

            if (gameManager.getGameStatus() == GameStatus.LOBBY) {
                Bukkit.getServer().sendMessage(Component.text(player.getName() + " is fucking ready", NamedTextColor.DARK_GREEN));
                if (allPlayerPortal() && !isCountdown) {
                    countdown();
                }
            } else {
                Bukkit.getServer().sendMessage(Component.text("tp zur overworld", NamedTextColor.DARK_GREEN));
                player.getInventory().clear();
                player.teleport(Bukkit.getWorld("game_overworld").getSpawnLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
            }
        }
    }

    private void countdown() {
        isCountdown = true;
        long period = 20;
        for (CustomChallenge challenge : trolling.getChallengeLoader().getCustomChallenges()) {
            if (challenge.getChallengeName().equals("FasterMinecraft") && challenge.isActive()) {
                period = 100;
            }
        }

        new BukkitRunnable() {
            int start_counter = 5;

            @Override
            public void run() {
                if (start_counter <= 0) {
                    isCountdown = false;
                    trolling.getGameManager().startGame();
                    cancel();
                    return;
                }

                Bukkit.getServer().sendMessage(Component.text("Game starts in " + start_counter + " seconds", NamedTextColor.GREEN));

                if (!allPlayerPortal()) {
                    isCountdown = false;
                    cancel();
                    return;
                }

                start_counter--;
            }
        }.runTaskTimer(trolling, 0, period);
    }

    private boolean allPlayerPortal() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            if (!world.getName().equalsIgnoreCase("ChallengesLobby_world")) {
                return false;
            }
            if (!player.getLocation().getBlock().getType().equals(Material.NETHER_PORTAL)) {
                return false;
            }
        }
        return true;
    }
}
