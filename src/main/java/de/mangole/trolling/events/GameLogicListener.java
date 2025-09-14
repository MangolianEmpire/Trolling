package de.mangole.trolling.events;

import de.mangole.trolling.*;
import de.mangole.trolling.gamemodes.*;
import de.mangole.trolling.utils.PlayerInitUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;

public class GameLogicListener implements Listener {

    private final Trolling trolling;
    private final GameManager gameManager;
    private ArrayList<GameModeBase> gameModes = new ArrayList<>();

    public GameLogicListener(Trolling trolling) {
        this.trolling = trolling;
        this.gameManager = trolling.getGameManager();
        this.gameModes.add(new GameModeFortnite(trolling));
        this.gameModes.add(new GameModeCasual(trolling));
        this.gameModes.add(new GameModeChallenge(trolling));
        this.gameModes.add(new GameModeCreative(trolling));
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
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        trolling.getTimerManager().getTimerCounter().addPlayer(player);
        if (gameManager.getGameStatus() == GameStatus.LOBBY) {
            PlayerInitUtils.initPlayerLobby(player);
        } else if (player.getLocation().getWorld().equals(WorldManager.lobbyWorld)) {
            if (gameManager.getGameMode() == GameMode.FORTNITE) {
                PlayerInitUtils.initPlayerFortnite(player);
            } else {
                PlayerInitUtils.initPlayerGame(player);
            }
        }

        if (gameManager.getGameStatus() == GameStatus.LOST) {
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        } else if (gameManager.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(org.bukkit.GameMode.CREATIVE);
        } else {
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onPauseMove(PlayerMoveEvent event) {
        if (gameManager.getGameStatus() == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseInteract(PlayerInteractEvent event) {
        if (gameManager.getGameStatus() == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseHunger(FoodLevelChangeEvent event) {
        if (gameManager.getGameStatus() == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseBreak(BlockBreakEvent event) {
        if (gameManager.getGameStatus() == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPauseDamage(EntityDamageEvent event) {
        if (gameManager.getGameStatus() == GameStatus.PAUSED) {
            event.setCancelled(true);
        }
    }

}
