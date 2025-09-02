package de.mangole.trolling.events;

import de.mangole.trolling.*;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.TimeSkipEvent;

import static de.mangole.trolling.GameManager.gameMode;
import static de.mangole.trolling.GameManager.gameStatus;

public class GameLogicListener implements Listener {

    private final Trolling trolling;

    public GameLogicListener(Trolling trolling) {
        this.trolling = trolling;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        World gameOverWorld = Bukkit.getWorld("game_overworld");
        if (gameOverWorld == null) return;

        if (event.getRespawnLocation().getWorld() == null
                || !event.getRespawnLocation().getWorld().equals(gameOverWorld)) {
            event.setRespawnLocation(gameOverWorld.getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        GameManager gameManager = trolling.getGameManager();
        if (GameManager.gameMode == GameMode.CHALLENGE) {
            gameManager.loseGame();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        trolling.getTimer().addPlayer(player);
        if (gameStatus == GameStatus.LOBBY) {
            player.teleport(WorldManager.lobbySpawn);
            World world = player.getWorld();
            world.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            player.getInventory().clear();
            player.getInventory().setItem(0, Data.lobbySpawnTeleporter);
            player.getInventory().setItem(4, Data.lobbySettings);
            player.getInventory().setItem(8, Data.lobbyGameModeChanger);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setExperienceLevelAndProgress(0);
            player.clearActivePotionEffects();
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/advancement revoke " + player.getName() + " everything");
        } else if (player.getLocation().getWorld().equals(WorldManager.lobbyWorld)) {
            player.teleport(Bukkit.getWorld("game_overworld").getSpawnLocation());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setExperienceLevelAndProgress(0);
            player.getInventory().clear();
        }

        if (gameStatus == GameStatus.LOST) {
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        } else if (gameMode == GameMode.CREATIVE) {
            player.setGameMode(org.bukkit.GameMode.CREATIVE);
        } else {
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onPauseSpawn(PlayerMoveEvent event) {
        if (gameStatus == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseInteract(PlayerInteractEvent event) {
        if (gameStatus == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseHunger(FoodLevelChangeEvent event) {
        if (gameStatus == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseBreak(BlockBreakEvent event) {
        if (gameStatus == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseDamage(EntityDamageEvent event) {
        if (gameStatus == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

}
