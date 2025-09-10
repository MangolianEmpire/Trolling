package de.mangole.trolling;

import de.mangole.trolling.events.GameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class GameManager {

    private final Trolling plugin;
    private final File statusFile;
    private final YamlConfiguration config;

    private GameStatus gameStatus = GameStatus.LOBBY;
    private GameMode gameMode = GameMode.CASUAL;

    public GameManager(Trolling plugin) {
        this.plugin = plugin;
        this.statusFile = new File(plugin.getDataFolder(), "status.yml");
        this.config = YamlConfiguration.loadConfiguration(statusFile);
        loadStatus();
    }

    public void startGame() {
        if (gameStatus == GameStatus.LOBBY) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.RUNNING));
            gameStatus = GameStatus.RUNNING;
            plugin.getTimer().start();
        }
    }

    public void pauseGame() {
        if (gameStatus == GameStatus.RUNNING) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.PAUSED));
            gameStatus = GameStatus.PAUSED;
            plugin.getTimer().pause();
        }
    }

    public void resumeGame() {
        if (gameStatus == GameStatus.PAUSED) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.RUNNING));
            gameStatus = GameStatus.RUNNING;
            plugin.getTimer().resume();
        }
    }

    public void loseGame() {
        if (gameStatus != GameStatus.LOBBY) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.LOST));
            gameStatus = GameStatus.LOST;
            plugin.getTimer().pause();
        }
    }

    public void stopGame() {
        if (gameStatus != GameStatus.LOBBY) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.LOBBY));
            gameStatus = GameStatus.LOBBY;
            plugin.getTimer().stop();
        }
    }

    public void switchGameMode() {
        GameMode newGameMode;
        if (gameMode == GameMode.CASUAL) {
            newGameMode = GameMode.CREATIVE;
        } else if (gameMode == GameMode.CREATIVE) {
            newGameMode = GameMode.CHALLENGE;
        } else if (gameMode == GameMode.CHALLENGE) {
            newGameMode = GameMode.FORTNITE;
        } else {
            newGameMode = GameMode.CASUAL;
        }
        Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, newGameMode, gameStatus, gameStatus));
        gameMode = newGameMode;
    }

    public void loadStatus() {
        if (statusFile.exists()) {
            String status = config.getString("gameStatus", "LOBBY");
            String mode = config.getString("gameMode", "CASUAL");
            try {
                gameStatus = GameStatus.valueOf(status);
                gameMode = GameMode.valueOf(mode);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Ungültiger Status in Datei, fallback auf LOBBY.");
                gameStatus = GameStatus.LOBBY;
                gameMode = GameMode.CASUAL;
            }
        } else {
            gameStatus = GameStatus.LOBBY;
            gameMode = GameMode.CASUAL;
        }
        plugin.getLogger().info("Spielstatus geladen: " + gameStatus + " " + gameMode);
    }

    public void saveStatus() {
        config.set("gameStatus", gameStatus.toString());
        config.set("gameMode", gameMode.toString());
        try {
            config.save(statusFile);
            plugin.getLogger().info("Spielstatus gespeichert: " + gameStatus + " " + gameMode);
        } catch (IOException e) {
            plugin.getLogger().severe("Fehler beim Speichern von status.yml: " + e.getMessage());
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }
}

