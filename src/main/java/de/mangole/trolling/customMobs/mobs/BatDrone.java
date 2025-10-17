package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.*;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BatDrone extends CustomMob {

    private static BukkitTask batTask = null;
    private static final Set<Bat> activeBats = new HashSet<>();

    public BatDrone(Trolling trolling) {
        super(trolling, "bat_drone", Bat.class);
    }

    @Override
    public void spawn(Location location) {
        super.spawn(location);

        Bat bat = (Bat) getMob();
        bat.setSilent(true);
        bat.setAwake(true);
        bat.setAI(true);
        bat.setRemoveWhenFarAway(true);

        NamespacedKey key = new NamespacedKey(trolling, "custom_mob_id");
        bat.getPersistentDataContainer().set(key, PersistentDataType.STRING, getId());

        activeBats.add(bat);

        if (batTask == null) {
            batTask = startBatTracking();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Spieler in der Nähe von aktiven BatDrones erkennen
        for (Bat bat : new HashSet<>(activeBats)) {
            if (!bat.isValid() || bat.isDead()) {
                activeBats.remove(bat);
                continue;
            }

            if (bat.getLocation().distanceSquared(event.getPlayer().getLocation()) <= 100) {
                // Wir könnten hier Effekte abspielen oder weiteres Verhalten triggern
            }
        }
    }

    private BukkitTask startBatTracking() {
        if (batTask != null && !batTask.isCancelled()) return batTask;

        return new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Bat> it = activeBats.iterator();
                while (it.hasNext()) {
                    Bat bat = it.next();
                    if (bat == null || !bat.isValid() || bat.isDead()) {
                        it.remove();
                        continue;
                    }

                    Player target = getNearestVisiblePlayer(bat);
                    if (target != null) updateBatBehavior(bat, target);
                }
            }
        }.runTaskTimer(trolling, 0L, 5L);
    }

    private Player getNearestVisiblePlayer(Bat bat) {
        double closest = Double.MAX_VALUE;
        Player nearest = null;

        for (Player p : bat.getWorld().getPlayers()) {
            if (p.getGameMode() != GameMode.SURVIVAL) continue;
            if (!bat.hasLineOfSight(p)) continue;

            double dist = p.getLocation().distanceSquared(bat.getLocation());
            if (dist < closest && dist < 100) {
                closest = dist;
                nearest = p;
            }
        }
        return nearest;
    }

    private void updateBatBehavior(Bat bat, Player target) {
        Location batLoc = bat.getLocation();
        Location targetLoc = target.getLocation();
        double distance = batLoc.distance(targetLoc);

        if (distance <= 2.0) {
            explodeBat(bat);
            return;
        }

        Vector dir = targetLoc.toVector().subtract(batLoc.toVector()).normalize();
        bat.setVelocity(dir.multiply(0.6));

        bat.getWorld().spawnParticle(Particle.PORTAL, batLoc, 3, 0.2, 0.2, 0.2, 0.02);
        bat.getWorld().playSound(batLoc, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 0.2f, 2.0f);
    }

    private void explodeBat(Bat bat) {
        if (bat.isDead()) return;

        Location loc = bat.getLocation();
        bat.getWorld().createExplosion(loc, 3.0f, false, true, bat);
        bat.remove();
        activeBats.remove(bat);
    }

    @CustomMobEvent
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Bat bat)) return;
        if (!activeBats.contains(bat)) return; // nur eigene BatDrones

        explodeBat(bat);
    }
}
