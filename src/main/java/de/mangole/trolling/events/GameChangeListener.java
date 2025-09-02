package de.mangole.trolling.events;

import de.mangole.trolling.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

public class GameChangeListener implements Listener {

    private final Trolling trolling;

    public GameChangeListener(final Trolling trolling) {
        this.trolling = trolling;
    }

    @EventHandler
    public void onStatusChange(GameChangeEvent event) {
        GameStatus oldStatus = event.getOldStatus();
        GameStatus newStatus = event.getNewStatus();

        if (oldStatus == newStatus) return;

        if (oldStatus == GameStatus.LOBBY && newStatus == GameStatus.RUNNING) {
            World gameOverWorld = Bukkit.getWorld("game_overworld");
            Bukkit.getWorlds().forEach(world -> {world.setDifficulty(Difficulty.HARD);});
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.getInventory().clear();
                player.teleport(gameOverWorld.getSpawnLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
            });
            gameOverWorld.setTime(1000);
            gameOverWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }

        if (newStatus == GameStatus.LOBBY) {
            trolling.getServer().getScheduler().cancelTasks(trolling);
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.getInventory().clear();
                player.getInventory().setItem(0, Data.lobbySpawnTeleporter);
                player.getInventory().setItem(4, Data.lobbySettings);
                player.getInventory().setItem(8, Data.lobbyGameModeChanger);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setExperienceLevelAndProgress(0);
                player.teleport(WorldManager.lobbySpawn);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
                player.clearActivePotionEffects();
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "/advancement revoke " + player.getName() + " everything");
                if (GameManager.gameMode == de.mangole.trolling.GameMode.CREATIVE) {
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
                player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_DEATH, 1F, 1F);
            });
            Bukkit.getServer().sendMessage(Component.text("You lost the Game. It will automatically stop in 60 seconds", NamedTextColor.GREEN));

            trolling.getServer().getScheduler().runTaskLater(trolling, new Runnable() {
                @Override
                public void run() {
                    if (GameManager.gameStatus == GameStatus.LOST) {
                        trolling.getGameManager().stopGame();
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
