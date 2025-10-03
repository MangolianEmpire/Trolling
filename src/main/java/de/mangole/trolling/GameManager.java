package de.mangole.trolling;

import de.mangole.trolling.events.GameChangeEvent;
import de.mangole.trolling.gamemodes.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameManager {

    private final Trolling trolling;
    private final File statusFile;
    private final YamlConfiguration config;
    private ArrayList<GameModeBase> gameModes = new ArrayList<>();

    private GameStatus gameStatus = GameStatus.LOBBY;
    private GameMode gameMode = GameMode.CASUAL;

    public GameManager(Trolling trolling) {
        this.trolling = trolling;
        this.statusFile = new File(trolling.getDataFolder(), "status.yml");
        this.config = YamlConfiguration.loadConfiguration(statusFile);
        this.gameModes.add(new GameModeFortnite(trolling, this));
        this.gameModes.add(new GameModeCasual(trolling, this));
        this.gameModes.add(new GameModeChallenge(trolling, this));
        this.gameModes.add(new GameModeCreative(trolling, this));
        loadStatus();
    }

    public void startGame() {
        if (gameStatus == GameStatus.LOBBY) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.RUNNING));
            gameStatus = GameStatus.RUNNING;
            trolling.getTimerManager().startTimer();
        }
    }

    public void pauseGame() {
        if (gameStatus == GameStatus.RUNNING) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.PAUSED));
            gameStatus = GameStatus.PAUSED;
            trolling.getTimerManager().pauseTimer();
        }
    }

    public void resumeGame() {
        if (gameStatus == GameStatus.PAUSED) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.RUNNING));
            gameStatus = GameStatus.RUNNING;
            trolling.getTimerManager().resumeTimer();
        }
    }

    public void loseGame() {
        if (gameStatus != GameStatus.LOBBY) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.LOST));
            gameStatus = GameStatus.LOST;
            trolling.getTimerManager().pauseTimer();
        }
    }

    public void stopGame() {
        if (gameStatus != GameStatus.LOBBY) {
            Bukkit.getPluginManager().callEvent(new GameChangeEvent(gameMode, gameMode, gameStatus, GameStatus.LOBBY));
            gameStatus = GameStatus.LOBBY;
            trolling.getTimerManager().stopTimer();
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
                trolling.getLogger().warning("Ungültiger Status in Datei, fallback auf LOBBY.");
                gameStatus = GameStatus.LOBBY;
                gameMode = GameMode.CASUAL;
            }
        } else {
            gameStatus = GameStatus.LOBBY;
            gameMode = GameMode.CASUAL;
        }
        trolling.getLogger().info("Spielstatus geladen: " + gameStatus + " " + gameMode);
    }

    public void saveStatus() {
        config.set("gameStatus", gameStatus.toString());
        config.set("gameMode", gameMode.toString());
        try {
            config.save(statusFile);
            trolling.getLogger().info("Spielstatus gespeichert: " + gameStatus + " " + gameMode);
        } catch (IOException e) {
            trolling.getLogger().severe("Fehler beim Speichern von status.yml: " + e.getMessage());
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }
}

