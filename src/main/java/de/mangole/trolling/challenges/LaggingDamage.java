package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class LaggingDamage extends CustomChallenge {

    private HashMap<UUID, Double> storedDamage;
    private HashMap<UUID, Integer> damageCountdown;
    private Set<UUID> challengeDamage;
    private BukkitTask damageTask;

    public LaggingDamage(Trolling trolling) {
        super(trolling, "LaggingDamage");
    }

    @Override
    protected void onActivate() {
        storedDamage = new HashMap<>();
        damageCountdown = new HashMap<>();
        challengeDamage = new HashSet<>();
        damageTask = startDamageTask();
    }

    @Override
    protected void onDeactivate() {
        if (storedDamage != null) {
            storedDamage.clear();
            storedDamage = null;
        }
        if (damageCountdown != null) {
            damageCountdown.clear();
            damageCountdown = null;
        }
        if (challengeDamage != null) {
            challengeDamage.clear();
            challengeDamage = null;
        }
        if (damageTask != null) {
            damageTask.cancel();
            damageTask = null;
        }
    }

    private BukkitTask startDamageTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (!damageCountdown.containsKey(uuid)) continue;
                    if (damageCountdown.get(uuid) <= 0) {
                        damageCountdown.remove(uuid);
                        double damage = storedDamage.get(uuid);
                        storedDamage.remove(uuid);
                        damagePlayer(player, damage);
                    } else {
                        damageCountdown.put(uuid, damageCountdown.get(uuid) - 1);
                    }
                }
            }
        }.runTaskTimer(trolling, 0L, 20L);
    }

    private void damagePlayer(Player player, double damage) {
        challengeDamage.add(player.getUniqueId());
        player.damage(damage);
        Bukkit.getScheduler().runTaskLater(trolling, () -> challengeDamage.remove(player.getUniqueId()), 1L);
    }

    @ChallengeEvent
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        UUID uuid = player.getUniqueId();
        if (challengeDamage.contains(uuid)) return;

        double damage = event.getFinalDamage();

        if (!storedDamage.containsKey(uuid)) {
            storedDamage.put(uuid, damage);
        } else {
            storedDamage.put(uuid, storedDamage.get(uuid) + damage);
        }

        if (!damageCountdown.containsKey(uuid)) {
            int random = new Random().nextInt(10);
            damageCountdown.put(uuid, 10 + random);
        }

        event.setCancelled(true);
    }


}
