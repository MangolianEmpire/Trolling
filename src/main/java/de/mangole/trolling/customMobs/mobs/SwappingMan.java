package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

import java.util.Random;

public class SwappingMan extends CustomMob {

    public SwappingMan(Trolling trolling) {
        super(trolling, "swapping_man", Enderman.class);
    }

    @CustomMobEvent
    public void onEndermanHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Random random = new Random();
        if (random.nextDouble() < 0.2) {
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 1, 1, 1);

            Location loc = player.getLocation().clone().add(0, 10, 0);
            player.teleport(loc);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        }
    }

    @CustomMobEvent
    public void onEndermanTp(EntityTeleportEvent event) {
        Enderman enderman = (Enderman) this.mob;

        Player nearest = null;
        double closestDist = Double.MAX_VALUE;

        for (Player p : enderman.getWorld().getPlayers()) {
            double dist = p.getLocation().distance(enderman.getLocation());
            if (dist < 15 && dist < closestDist) {
                closestDist = dist;
                nearest = p;
            }
        }

        if (nearest == null) return;

        Random random = new Random();

        if (random.nextDouble() < 0.3) {

            Location endermanOldLoc = enderman.getLocation().clone();
            Location playerLoc = nearest.getLocation().clone();
            nearest.teleport(endermanOldLoc);
            event.setTo(playerLoc);

            nearest.getWorld().playSound(playerLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            nearest.getWorld().spawnParticle(Particle.PORTAL, playerLoc, 50, 1, 1, 1);
        }
    }
}
