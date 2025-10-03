package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

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

    @ChallengeEvent
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        double finalDamage = e.getFinalDamage();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                p.setHealth(Math.max(player.getHealth() - finalDamage, 0));
            }
        }
    }

    @ChallengeEvent
    public void onHeal(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        double amount = e.getAmount();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                p.setHealth(Math.min(p.getHealth() + amount, p.getMaxHealth()));
            }
        }
    }

    @ChallengeEvent
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
