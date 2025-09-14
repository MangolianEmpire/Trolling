package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class MiniBosses extends CustomChallenge {

    public MiniBosses(Trolling trolling) {
        super(trolling, "Minibosses");
    }

    @Override
    protected void onActivate() {

    }

    @Override
    protected void onDeactivate() {

    }

    boolean isSpawningBoss = false;

    @EventHandler
    public void onEntitiySpawn(EntitySpawnEvent event) {
        if (isSpawningBoss) {
            return;
        }
        @NotNull Entity mob = event.getEntity();
        Random random = new Random();

        if (mob instanceof Slime && random.nextDouble() <= 0.08) {
            event.setCancelled(true);
            isSpawningBoss = true;
            spawnSlimeBoss(mob.getLocation());
            isSpawningBoss = false;
        }

        if (mob instanceof Blaze && random.nextDouble() <= 0.05) {
            event.setCancelled(true);
            isSpawningBoss = true;
            spawnBlazeBoss(mob.getLocation());
            isSpawningBoss = false;
        }
    }


    // Mother Slime ____________________________________________________________________________________________________

    private void spawnSlimeBoss(Location loc) {
        World world = loc.getWorld();
        Slime bossSlime = (Slime) world.spawnEntity(loc, EntityType.SLIME);
        bossSlime.setSize(10);
        bossSlime.customName(Component.text("Mother Slime", NamedTextColor.GREEN));
        bossSlime.setCustomNameVisible(true);
        bossSlime.setMetadata("isBoss", new FixedMetadataValue(trolling, true));
        bossSlime.setPersistent(true);
        bossSlime.setAI(true);
    }
    @EventHandler
    public void onBossSlimeHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Slime slime)) return;
        if (!slime.hasMetadata("isBoss")) return;

        Location loc = slime.getLocation();

        isSpawningBoss = true;
        Slime miniSlime = (Slime) loc.getWorld().spawnEntity(loc, EntityType.SLIME);
        isSpawningBoss = false;
        miniSlime.setSize(2);
        miniSlime.customName(Component.text("Baby Slime", NamedTextColor.GREEN));
        miniSlime.setMetadata("isMinion", new FixedMetadataValue(trolling, true));
        miniSlime.setCustomNameVisible(true);
        miniSlime.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 3));
    }

    @EventHandler
    public void onBossSlimeSplit(SlimeSplitEvent event) {
        Slime slime = event.getEntity();
        if (slime.hasMetadata("isBoss") || slime.hasMetadata("isMinion")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBossSlimeDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Slime slime)) return;
        if (slime.hasMetadata("isBoss")) {
            // Create green leather boots
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
            NamespacedKey key = new NamespacedKey(trolling, "slime_boots");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            meta.displayName(Component.text("Slime Boots", NamedTextColor.GREEN));
            meta.addEnchant(Enchantment.UNBREAKING, 5, true);
            meta.lore(Collections.singletonList(Component.text("Jump while crouching for a boost!")));
            meta.setColor(Color.GREEN);
            boots.setItemMeta(meta);
            event.getDrops().add(boots);
        }
    }

    @EventHandler
    public void onPlayerJumpWhileSneaking(PlayerToggleSneakEvent event) {
        var player = event.getPlayer();

        // Must wear leather boots
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null || boots.getType() != Material.LEATHER_BOOTS) return;

        // Check if they're the Slime Boots
        ItemMeta meta = boots.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(trolling, "slime_boots");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;

        if (!player.isSneaking()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 4));
            player.getWorld().playSound(player.getLocation(), "entity.slime.jump", 1f, 1f);
            player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation(), 10);
        } else {
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        }
    }



    // Ashen Tyrant __________________________________________________________________________________________________

    private void spawnBlazeBoss(@NotNull Location loc) {
        World world = loc.getWorld();
        Blaze bossBlaze = (Blaze) world.spawnEntity(loc, EntityType.BLAZE);
        bossBlaze.customName(Component.text("Ashen Tyrant", NamedTextColor.GOLD));
        bossBlaze.setCustomNameVisible(true);
        bossBlaze.setMetadata("isBoss", new FixedMetadataValue(trolling, true));
        bossBlaze.setPersistent(true);
        Objects.requireNonNull(bossBlaze.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(60);
        bossBlaze.setHealth(60);
        bossBlaze.setAI(true);

        bossBlaze.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1, true, false));
        bossBlaze.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, true, false));

        startFireAura(bossBlaze);
        startFireballAttack(bossBlaze);
        startParticles(bossBlaze);
    }

    @EventHandler
    public void onBossBlazeDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Blaze blaze)) return;
        if (blaze.hasMetadata("isBoss")) {
            ItemStack fireball = new ItemStack(Material.BLAZE_POWDER);
            ItemMeta itemMeta = fireball.getItemMeta();
            NamespacedKey key = new NamespacedKey(trolling, "fire-breath");
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            itemMeta.displayName(Component.text("Fire Breath", NamedTextColor.GOLD));
            itemMeta.setEnchantmentGlintOverride(true);
            itemMeta.lore(Collections.singletonList(Component.text("Rigth-click to spit Fire!")));
            fireball.setItemMeta(itemMeta);
            event.getDrops().add(fireball);
        }
    }

    private void startFireAura(Blaze blaze) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!blaze.isValid() || blaze.isDead()) {
                    cancel();
                    return;
                }

                for (Player p : blaze.getWorld().getNearbyPlayers(blaze.getLocation(), 5)) {
                    p.setFireTicks(40); // Set on fire for 2 seconds
                }
            }
        }.runTaskTimer(trolling, 0L, 40L); // Every 2 seconds
    }

    private void startFireballAttack(Blaze blaze) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!blaze.isValid() || blaze.isDead()) {
                    cancel();
                    return;
                }

                if (blaze.getTarget() != null && blaze.hasLineOfSight(blaze.getTarget())) {
                    Fireball fireball = blaze.launchProjectile(Fireball.class);
                    fireball.setIsIncendiary(true);
                    fireball.setYield(2.0F); // Bigger explosion than normal
                    fireball.setVelocity(blaze.getEyeLocation().getDirection().multiply(1.2));
                }
            }
        }.runTaskTimer(trolling, 40L, 100L); // Starts after 2s, fires every 5s
    }

    private void startParticles(Blaze blaze) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!blaze.isValid() || blaze.isDead()) {
                    cancel();
                    return;
                }
                blaze.getWorld().spawnParticle(Particle.FLAME, blaze.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
                blaze.getWorld().spawnParticle(Particle.DRIPPING_LAVA, blaze.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);
                blaze.getWorld().spawnParticle(Particle.LAVA, blaze.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.01);
            }
        }.runTaskTimer(trolling, 0L, 10L); // Every 0.5 seconds
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Blaze blaze)) return;
        if (!blaze.hasMetadata("isBoss")) return;

        if (event.getDamager() instanceof Arrow) {
            event.setCancelled(true);
            blaze.getWorld().spawnParticle(Particle.EXPLOSION, event.getEntity().getLocation(), 10);
            blaze.getWorld().playSound(blaze.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 1, 1);
        }
    }

    @EventHandler
    public void onPlayerUseFireBreath(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.BLAZE_POWDER) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // Check if it's our custom item
        NamespacedKey key = new NamespacedKey(trolling, "fire-breath");
        Byte value = meta.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        if (value == null || value != 1) return;

        if (player.getCooldown(Material.BLAZE_POWDER) > 0) return;

        // Cancel any default interaction
        event.setCancelled(true);

        player.setCooldown(Material.BLAZE_POWDER, 400);

        // Launch fireball
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(true);
        fireball.setYield(1.5f); // Explosion power
        fireball.setVelocity(player.getLocation().getDirection().multiply(1.2));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.2f);
    }

}
