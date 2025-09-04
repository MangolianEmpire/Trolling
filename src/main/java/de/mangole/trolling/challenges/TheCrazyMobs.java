package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import de.mangole.trolling.utils.CustomChallengeItem;
import org.bukkit.*;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class TheCrazyMobs extends CustomChallenge {

    public TheCrazyMobs(Plugin plugin) {
        super(plugin, "TheCrazyMobs");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;

        Creeper creeper = (Creeper) event.getEntity();
        Location explosionLoc = creeper.getLocation();

        double radius = 10.0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(explosionLoc.getWorld())
                    && player.getLocation().distance(explosionLoc) <= radius) {

                Vector direction = player.getLocation().toVector().subtract(explosionLoc.toVector()).normalize();

                double power = 30.0;
                Vector velocity = direction.multiply(power);
                velocity.setY(velocity.getY() + 1.0);

                player.setVelocity(velocity);
            }
        }
    }

    @EventHandler
    public void onSkeletonShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;

        final int[] count = {0};
        Bukkit.getScheduler().runTaskTimer(Trolling.plugin, task -> {
            skeleton.launchProjectile(Arrow.class);
            count[0]++;
            if (count[0] >= 5) {
                task.cancel();
            }
        }, 4L, 4L);
    }

    @EventHandler
    public void onZombieDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (zombie.isBaby()) return;

        World world = zombie.getWorld();
        Location location = zombie.getLocation();
        Random random = new Random();
        int zombie_amount = random.nextInt(4);
        zombie_amount = Math.max(zombie_amount, 2);
        for (int i = 0; i < zombie_amount; i++) {
            Zombie baby = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
            baby.setBaby(true);
            baby.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
        }
    }

    @EventHandler
    public void onEndermanHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Enderman)) return;

        Random random = new Random();
        if (random.nextDouble() < 0.2) {
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 1, 1, 1);

            Location loc = player.getLocation().clone().add(0, 10, 0);
            player.teleport(loc);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        }
    }

    @EventHandler
    public void onEndermanTp(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Enderman enderman)) return;

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

    @EventHandler
    public void onSpiderAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Spider spider)) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 5));
        spider.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 5));
    }

    @EventHandler
    public void onSheepDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Sheep sheep)) return;

        Random random = new Random();

        if (random.nextDouble() < 0.3) {
            Location loc = sheep.getLocation().clone();
            World world = sheep.getWorld();

            // TNT-Richtungen (N, O, S, W)
            Vector[] directions = {
                    new Vector(1, 0.3, 0),   // Osten
                    new Vector(-1, 0.3, 0),  // Westen
                    new Vector(0, 0.3, 1),   // Süden
                    new Vector(0, 0.3, -1)   // Norden
            };

            for (Vector dir : directions) {
                TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc, EntityType.TNT);
                tnt.setFuseTicks(40); // 3 Sekunden bis Explosion (20 Ticks = 1 Sekunde)
                tnt.setVelocity(dir.multiply(0.5)); // Stärke anpassen, z.B. 0.5 für "nicht so weit"
            }
        }
    }

    @EventHandler
    public void onChickenDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.CHICKEN) return;
        Location loc = event.getEntity().getLocation();

        double radius = 3.0;
        double knockback_strength = 3.0;
        double damage = 6.0;

        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate();

        for (Entity nearby: loc.getWorld().getNearbyEntities(loc, radius, radius, radius)){
            ((LivingEntity)nearby).damage(damage);

            if (nearby != event.getEntity()){
                Vector knockback = nearby.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(knockback_strength);
                nearby.setVelocity(nearby.getVelocity().add(knockback));
            }
        }

    }

}
