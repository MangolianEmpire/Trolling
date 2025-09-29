package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Communism extends CustomChallenge {

    public Communism(Trolling trolling) {
        super(trolling, "Communism");
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


        double newHealth = player.getHealth();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                p.setHealth(Math.min(newHealth, p.getMaxHealth()));
            }
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        Bukkit.getScheduler().runTaskLater(trolling, () -> {
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

        Bukkit.getScheduler().runTaskLater(trolling, () -> {
            int newFood = player.getFoodLevel();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != player) {
                    p.setFoodLevel(newFood);
                }
            }
        }, 1L);
    }

}
