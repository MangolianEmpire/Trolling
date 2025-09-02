package de.mangole.trolling.utils;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.Trolling;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

public class TimeCounter {

    private long seconds = 0;
    private BukkitTask counter;
    private final Trolling trolling;
    private boolean paused = false;

    private Scoreboard scoreboard;
    private Objective objective;

    public TimeCounter(Trolling trolling) {
        this.trolling = trolling;
        setupScoreboard();
    }

    private void setupScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("timer", "dummy", ChatColor.GREEN + "Time");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void start() {
        paused = false;
        seconds = 0;
        long period = 20;
        for (CustomChallenge challenge : trolling.getChallengeLoader().getCustomChallenges()) {
            if (challenge.getChallengeName().equals("FasterMinecraft") && challenge.isActive()) {
                period = 100;
            }
        }

        if (counter != null) {
            counter.cancel();
        }

        counter = new BukkitRunnable() {
            @Override
            public void run() {
                if (!paused) {
                    seconds++;
                    updateScoreboard(seconds);
                }
            }
        }.runTaskTimer(trolling, 0L, period);
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void stop() {
        if (counter != null) {
            counter.cancel();
        }
        seconds = 0;
        updateScoreboard(seconds);
    }

    private void updateScoreboard(long seconds) {
        scoreboard.getEntries().forEach(scoreboard::resetScores);

        Score score = objective.getScore(ChatColor.YELLOW + formatTime(seconds));
        score.setScore(1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        seconds -= hours * 3600;
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }

    public void addPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }
}

