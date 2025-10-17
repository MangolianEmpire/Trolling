package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class ExplodingChicken extends CustomMob {

    public ExplodingChicken(Trolling trolling) {
        super(trolling, "exploding_chicken", Chicken.class);
    }

    @CustomMobEvent
    public void onChickenDeath(EntityDeathEvent event) {
        Location loc = event.getEntity().getLocation();

        double radius = 1.5;
        double knockback_strength = 3.0;
        double damage = 6.0;

        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL)
                .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate();


        loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
            if (entity != event.getEntity()) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).damage(damage);
                }
                Vector direction = entity.getLocation().toVector().subtract(loc.toVector());
                if (direction.lengthSquared() > 0.0001) { // avoid zero-length vectors
                    Vector knockback = direction.normalize().multiply(knockback_strength);
                    entity.setVelocity(entity.getVelocity().add(knockback));
                }
            }
        });

    }
}
