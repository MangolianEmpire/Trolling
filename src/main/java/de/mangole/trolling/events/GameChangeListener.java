package de.mangole.trolling.events;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.PlayerInitUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class GameChangeListener implements Listener {

    private final Trolling trolling;
    private final GameManager gameManager;

    public GameChangeListener(final Trolling trolling) {
        this.trolling = trolling;
        this.gameManager = trolling.getGameManager();
    }

    @EventHandler
    public void onStatusChange(GameChangeEvent event) {
        GameStatus oldStatus = event.getOldStatus();
        GameStatus newStatus = event.getNewStatus();

        if (oldStatus == newStatus) return;

        if (oldStatus == GameStatus.LOBBY && newStatus == GameStatus.RUNNING) {
            World gameOverWorld = Bukkit.getWorld("game_overworld");
            Bukkit.getWorlds().forEach(world -> {
                world.setDifficulty(Difficulty.HARD);
                world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            });
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (gameManager.getGameMode() == de.mangole.trolling.GameMode.FORTNITE) {
                    PlayerInitUtils.initPlayerFortnite(player);
                } else {
                    PlayerInitUtils.initPlayerGame(player);
                }
            }
            gameOverWorld.setTime(1000);
        }

        if (newStatus == GameStatus.LOBBY) {
            trolling.getServer().getScheduler().cancelTasks(trolling);
            Bukkit.getOnlinePlayers().forEach(player -> {
                PlayerInitUtils.initPlayerLobby(player);
                if (gameManager.getGameMode() == de.mangole.trolling.GameMode.CREATIVE) {
                    player.setGameMode(GameMode.CREATIVE);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                }
            });
        }

        if (newStatus == GameStatus.PAUSED) {
            trolling.getServer().getServerTickManager().setFrozen(true);
            setTitlePaused(true);
        }
        if (newStatus != GameStatus.PAUSED) {
            trolling.getServer().getServerTickManager().setFrozen(false);
            setTitlePaused(false);
        }

        if (newStatus == GameStatus.LOST) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 0.3F);
            });
            Bukkit.getServer().sendMessage(Component.text("You lost the Game. It will automatically stop in 60 seconds", NamedTextColor.GREEN));

            trolling.getServer().getScheduler().runTaskLater(trolling, new Runnable() {
                @Override
                public void run() {
                    if (gameManager.getGameStatus() == GameStatus.LOST) {
                        gameManager.stopGame();
                    }
                }
            }, 20 * 60);
        }
    }

    @EventHandler
    public void onModeChange(GameChangeEvent event) {
        de.mangole.trolling.GameMode oldMode = event.getOldMode();
        de.mangole.trolling.GameMode newMode = event.getNewMode();

        if (oldMode == newMode) return;

        if (newMode == de.mangole.trolling.GameMode.CREATIVE) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setGameMode(GameMode.CREATIVE);
            });
            Bukkit.getServer().sendMessage(Component.text("Game Mode: CREATIVE", NamedTextColor.GREEN));
            return;
        }

        if (newMode == de.mangole.trolling.GameMode.CASUAL) {
            Bukkit.getServer().sendMessage(Component.text("Game Mode: CASUAL", NamedTextColor.GREEN));
        }

        if (newMode == de.mangole.trolling.GameMode.CHALLENGE) {
            Bukkit.getServer().sendMessage(Component.text("Game Mode: CHALLENGE", NamedTextColor.GREEN));
        }

        if (newMode == de.mangole.trolling.GameMode.FORTNITE) {
            Bukkit.getServer().sendMessage(Component.text("Game Mode: FORTNITE", NamedTextColor.GREEN));
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setExperienceLevelAndProgress(0);
        });
    }

    private BukkitTask pauseTask;

    public void setTitlePaused(boolean paused) {
        if (paused) {
            // Startet den Task, der alle 3 Sekunden "PAUSED" anzeigt
            pauseTask = Bukkit.getScheduler().runTaskTimer(trolling, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle(
                            ChatColor.RED + "PAUSED",
                            "",
                            10,  // FadeIn
                            40,  // Stay
                            10   // FadeOut
                    );
                }
            }, 0L, 60L); // alle 60 Ticks = 3 Sekunden
        } else {
            // Pause beendet -> Task stoppen und Titel entfernen
            if (pauseTask != null) {
                pauseTask.cancel();
                pauseTask = null;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.resetTitle();
            }
        }
    }

}
