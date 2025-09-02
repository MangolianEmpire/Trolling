package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class Communism extends CustomChallenge {

    public Communism(Plugin plugin) {
        super(plugin, "Communism");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double newHealth = player.getHealth();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != player) {
                    p.setHealth(Math.min(newHealth, p.getMaxHealth()));
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int newFood = player.getFoodLevel();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != player) {
                    p.setFoodLevel(newFood);
                }
            }
        }, 1L);
    }

}
