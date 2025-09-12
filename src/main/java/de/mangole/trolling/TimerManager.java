package de.mangole.trolling;

import de.mangole.trolling.utils.TimeCounter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TimerManager {

    private final Trolling trolling;
    private final File timerFile;
    private final YamlConfiguration config;
    private final TimeCounter timerCounter;

    public TimerManager(Trolling trolling) {
        this.trolling = trolling;
        this.timerFile = new File(trolling.getDataFolder(), "timer.yml");
        this.config = YamlConfiguration.loadConfiguration(timerFile);
        timerCounter = new TimeCounter(trolling);
        loadTimer();
    }

    public void loadTimer() {
        long timer;
        if (timerFile.exists()) {
            timer = config.getLong("Timer", 0);
        } else {
            timer = 0;
        }
        timerCounter.setSeconds(timer);

        if (trolling.getGameManager().getGameStatus() != GameStatus.LOBBY) {
            timerCounter.start();
        }
    }

    public void saveTimer() {
        config.set("Timer", timerCounter.getSeconds());
        try {
            config.save(timerFile);
        } catch (IOException e) {
            trolling.getLogger().severe("Fehler beim Speichern von timer.yml: " + e.getMessage());
        }
    }

    public void startTimer () {
        timerCounter.start();
    }

    public void pauseTimer () {
        timerCounter.pause();
    }

    public void resumeTimer () {
        timerCounter.resume();
    }

    public void stopTimer () {
        timerCounter.stop();
    }

    public TimeCounter getTimerCounter() {
        return timerCounter;
    }
}
