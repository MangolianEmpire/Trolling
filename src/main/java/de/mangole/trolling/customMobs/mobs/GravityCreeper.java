package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class GravityCreeper extends CustomMob {

    private boolean charging;

    public GravityCreeper(Trolling trolling) {
        super(trolling, "gravity_creeper", Creeper.class, "", false, 30);
        charging = false;
    }

    @CustomMobEvent
    private void onExplode(ExplosionPrimeEvent event) {
        event.setCancelled(true);
        if (charging) return;
        charging = true;

        Creeper creeper = (Creeper) getMob();
        World world = creeper.getWorld();

        int chargeTime = 60;
        double pullRadius = 10.0;


        world.playSound(creeper.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 0.8f);
        world.spawnParticle(Particle.PORTAL, creeper.getLocation(), 50, 1, 1, 1, 0.2);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!creeper.isValid() || creeper.isDead()) {
                    cancel();
                    return;
                }

                creeper.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5, 2, false, false));

                // Spieler und Entities anziehen
                List<Entity> nearby = creeper.getNearbyEntities(pullRadius, pullRadius, pullRadius);
                for (Entity e : nearby) {
                    if (e.equals(creeper)) continue;
                    Vector dir = creeper.getLocation().toVector().subtract(e.getLocation().toVector());
                    double distance = dir.length();
                    double strength = 1.0 / Math.max(1.0, distance);
                    e.setVelocity(e.getVelocity().add(dir.normalize().multiply(strength)));
                }

                if (ticks % 8 == 5) {
                    int amount = 3; // wie viele Blöcke pro Tick "reißen"
                    for (int i = 0; i < amount; i++) {
                        Location base = creeper.getLocation().clone();
                        int x = (int) (base.getX() + (Math.random() * pullRadius - (pullRadius / 2.0)));
                        int y = (int) (base.getY() - 1 + (Math.random() * pullRadius) - (pullRadius / 2.0)); // auch leicht unter dem Creeper
                        int z = (int) (base.getZ() + (Math.random() * pullRadius) - (pullRadius / 2.0));
                        Location blockLoc = new Location(base.getWorld(), x, y, z);
                        Block block = blockLoc.getBlock();

                        if (block.getType().isAir()) continue;
                        if (!block.getType().isSolid()) continue;
                        if (block.isLiquid()) continue;

                        Material mat = block.getType();
                        FallingBlock falling = base.getWorld().spawnFallingBlock(blockLoc.add(0.5, 0.5, 0.5), mat.createBlockData());
                        falling.setDropItem(false);
                        falling.setGravity(true);

                        // Richtung -> Creeper (anziehen)
                        Vector dir = creeper.getLocation().toVector().subtract(falling.getLocation().toVector()).normalize();
                        dir.multiply(0.6).setY(0.6 + Math.random() * 0.3);
                        falling.setVelocity(dir);

                        // Nach kurzer Zeit entfernen (damit es nicht rumliegt)
                        Bukkit.getScheduler().runTaskLater(trolling, () -> {
                            if (!falling.isDead()) falling.remove();
                        }, 40L); // 2 Sekunden Lebensdauer
                    }
                }

                // Effekte während des Aufladens
                world.spawnParticle(Particle.CRIT, creeper.getLocation(), 20, 1, 1, 1, 0.1);
                world.playSound(creeper.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, 1.5f);

                ticks++;
                if (ticks >= chargeTime) {
                    // Explosion auslösen
                    world.createExplosion(creeper.getLocation(), 5.0f, false, true, creeper);
                    world.playSound(creeper.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.7f);
                    world.spawnParticle(Particle.EXPLOSION, creeper.getLocation(), 5);
                    creeper.remove();

                    for (Entity e : nearby) {
                        if (e.equals(creeper)) continue;
                        Vector dir = e.getLocation().toVector().subtract(creeper.getLocation().toVector());
                        dir.add(new Vector(0, 1, 0)).normalize().multiply(5);
                        e.setVelocity(dir);
                    }

                    cancel();
                }
            }
        }.runTaskTimer(trolling, 0L, 1L);
    }
}
