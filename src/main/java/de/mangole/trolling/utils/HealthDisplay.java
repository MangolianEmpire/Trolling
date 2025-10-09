package de.mangole.trolling.utils;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class HealthDisplay implements Listener {

    private final Trolling trolling;
    private final Scoreboard scoreboard;
    private final Objective healthObjective;

    public HealthDisplay(Trolling trolling) {
        this.trolling = trolling;
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective old = scoreboard.getObjective("showhealth");
        if (old != null) old.unregister();

        this.healthObjective = scoreboard.registerNewObjective(
                "showhealth",
                "health",
                ChatColor.RED + "❤"
        );
        healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        setupPlayerTeam(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(trolling, () -> updateHealth(player), 1L);
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(trolling, () -> updateHealth(player), 1L);
        }
    }

    private void setupPlayerTeam(Player player) {
        String teamName = player.getName().length() > 15
                ? player.getName().substring(0, 15)
                : player.getName();

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }

        updateHealth(player);
        player.setScoreboard(scoreboard);
    }

    private void updateHealth(Player player) {
        double hp = Math.round(player.getHealth());

        Team team = scoreboard.getTeam(player.getName().length() > 15
                ? player.getName().substring(0, 15)
                : player.getName());

        if (team == null) return;

        team.setSuffix(ChatColor.GREEN + "" + (int) hp + " " + ChatColor.RED + "❤");
    }
}
