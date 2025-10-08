package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class TheCrazyMobs extends CustomChallenge {

    private BukkitTask batTask = null;
    private Set<Bat> activeBats = new HashSet<>();

    public TheCrazyMobs(Trolling trolling) {
        super(trolling, "TheCrazyMobs");
    }

    @Override
    protected void onActivate() {
        if (batTask != null) {
            batTask.cancel();
            batTask = null;
        }
        activeBats.clear();
        batTask = startBatTracking();
    }

    @Override
    protected void onDeactivate() {
        if (batTask != null) {
            batTask.cancel();
            batTask = null;
        }
        activeBats.clear();
    }

    @ChallengeEvent
    public void onBatDetectPlayer(PlayerMoveEvent event) {
        Location loc = event.getPlayer().getLocation();

        activeBats.addAll(loc.getNearbyEntitiesByType(Bat.class, 10));
    }

    private @NotNull BukkitTask startBatTracking() {
        return new BukkitRunnable() {
            long counter = 0;
            @Override
            public void run() {
                Iterator<Bat> it = activeBats.iterator();
                while (it.hasNext()) {
                    Bat bat = it.next();
                    if (!bat.isValid() || bat.isDead()) {
                        it.remove();
                        continue;
                    }

                    if (counter % 40 == 0) {
                        World world = bat.getWorld();
                        for (Player player : world.getNearbyPlayers(bat.getLocation(), 10))
                            player.playSound(player.getLocation(), Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0f, 2.0f);
                    }
                    counter += 5;

                    Player target = getNearestVisiblePlayer(bat);
                    if (target != null) {
                        updateBatBehavior(bat, target);
                    } else {
                        it.remove();
                    }
                }
            }
        }.runTaskTimer(trolling, 0L, 5L);
    }

    private Player getNearestVisiblePlayer(Bat bat) {
        double closestDist = Double.MAX_VALUE;
        Player nearest = null;
        for (Player player : bat.getWorld().getPlayers()) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) && bat.hasLineOfSight(player)) {
                double dist = player.getLocation().distanceSquared(bat.getLocation());
                if (dist < closestDist && dist <= 400) {
                    closestDist = dist;
                    nearest = player;
                }
            }
        }
        return nearest;
    }

    private void updateBatBehavior(Bat bat, Player target) {
        Location batLoc = bat.getLocation();
        Location targetLoc = target.getLocation();
        double distance = batLoc.distance(targetLoc);

        bat.setTarget(target);

        if (distance <= 2.0) {
            explodeBat(bat);
            return;
        }

        Vector direction = targetLoc.toVector().subtract(batLoc.toVector()).normalize();
        bat.setVelocity(direction.multiply(1.5));
    }

    private void explodeBat(Bat bat) {
        Location loc = bat.getLocation();
        bat.remove();
        World world = loc.getWorld();
        world.createExplosion(loc, 6.0f, true, true);
    }

    @ChallengeEvent
    public void onBatHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Bat bat)) return;
        explodeBat(bat);
    }

    @ChallengeEvent
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) return;

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

    @ChallengeEvent
    public void onSkeletonShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                skeleton.launchProjectile(Arrow.class);
                count++;
                if (count >= 4) {
                    cancel();
                }
            }
        }.runTaskTimer(trolling, 5L, 5L);
    }

    @ChallengeEvent
    public void onZombieDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (!zombie.isAdult()) return;

        World world = zombie.getWorld();
        Location location = zombie.getLocation();
        Random random = new Random();
        int zombie_amount = random.nextInt(3);
        zombie_amount = zombie_amount + 2;
        for (int i = 0; i < zombie_amount; i++) {
            Zombie baby = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
            baby.setBaby();
            baby.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
            baby.setHealth(1);
        }
    }

    @ChallengeEvent
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

    @ChallengeEvent
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

    @ChallengeEvent
    public void onSpiderAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Spider spider)) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 5));
        spider.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 5));
    }

    @ChallengeEvent
    public void onSheepDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Sheep sheep)) return;

        Random random = new Random();

        if (random.nextDouble() < 0.2) {
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

    @ChallengeEvent
    public void onCowDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Cow cow)) return;

        Random random = new Random();

        if (random.nextDouble() < 0.3) {
            event.setCancelled(true);
            cow.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 1));
            cow.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
        }
    }

    @ChallengeEvent
    public void onChickenDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.CHICKEN) return;
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

    @ChallengeEvent
    public void onGolemMove(EntityMoveEvent event) {
        if (!(event.getEntity() instanceof IronGolem golem)) return;
        if (golem.isDead() || !golem.isValid()) {
            return;
        }
        golem.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        Player target = getNearestPlayer(golem);
        if (target != null) {
            golem.setTarget(target);
        }
    }

    private Player getNearestPlayer(LivingEntity golem) {
        double nearestDistance = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player player : golem.getWorld().getPlayers()) {
            if (!player.isDead() && (player.getGameMode() == GameMode.SURVIVAL)) {
                double distance = player.getLocation().distanceSquared(golem.getLocation());
                if (distance < 400 && distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPlayer = player;
                }
            }
        }

        return nearestPlayer;
    }

    @ChallengeEvent
    public void onPigDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Pig pig)) return;
        if (!(pig.getKiller() instanceof Player killer)) return;

        if (pig.isAdult()) {
            for (ItemStack item : killer.getInventory().getContents()) {
                if (item != null) {
                    pig.getWorld().dropItemNaturally(pig.getLocation(), item);
                }
            }
            killer.getInventory().clear();
        } else {
            killer.getWorld().playSound(killer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            killer.getInventory().clear();
        }
    }

}
