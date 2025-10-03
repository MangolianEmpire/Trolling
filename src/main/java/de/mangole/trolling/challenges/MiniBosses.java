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
import org.bukkit.event.entity.*;
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
import java.util.List;
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

        if (mob instanceof Slime && random.nextDouble() <= 0.07) {
            if (mob.getType() == EntityType.MAGMA_CUBE) return;
            event.setCancelled(true);
            isSpawningBoss = true;
            spawnSlimeBoss(mob.getLocation());
            isSpawningBoss = false;
        }

        if (mob instanceof Blaze && random.nextDouble() <= 0.025) {
            event.setCancelled(true);
            isSpawningBoss = true;
            spawnBlazeBoss(mob.getLocation());
            isSpawningBoss = false;
        }

        if (mob instanceof Skeleton && random.nextDouble() <= 0.03) {
            event.setCancelled(true);
            isSpawningBoss = true;
            spawnNecromancer(mob.getLocation());
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


    // Dark Necromancer

    public void spawnNecromancer(@NotNull Location loc) {
        WitherSkeleton necromancer = (WitherSkeleton) loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);

        necromancer.customName(Component.text("Dark Necromancer", NamedTextColor.GRAY));
        necromancer.setCustomNameVisible(true);
        necromancer.getEquipment().setItemInMainHand(new ItemStack(Material.ENCHANTED_BOOK));
        necromancer.getEquipment().setItemInMainHandDropChance(0f); // Don't drop book
        necromancer.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET)); // Optional cosmetic
        necromancer.setMetadata("isBoss", new FixedMetadataValue(trolling, true));
        necromancer.setShouldBurnInDay(false); // Sunlight immunity
        necromancer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1, false, false));
        Objects.requireNonNull(necromancer.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(80);
        necromancer.setHealth(80);

        startNecromancing(necromancer);
        startSoulParticles(necromancer);
    }

    private void startSoulParticles(WitherSkeleton necromancer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!necromancer.isValid() || necromancer.isDead()) {
                    cancel();
                    return;
                }
                necromancer.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, necromancer.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
                necromancer.getWorld().spawnParticle(Particle.SCULK_SOUL, necromancer.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);
            }
        }.runTaskTimer(trolling, 0L, 10L); // Every 0.5 seconds
    }

    private void startNecromancing(WitherSkeleton necromancer) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (necromancer == null || necromancer.isDead() || !necromancer.isValid()) {
                    this.cancel(); // Stop the task
                    return;
                }

                if (necromancer.getTarget() == null) {
                    return;
                }

                Location loc = necromancer.getLocation();
                necromancer.getWorld().playSound(necromancer.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 2);


                isSpawningBoss = true;
                // Skeleton 1: Iron Axe
                Skeleton axeSkeleton = (Skeleton) loc.getWorld().spawnEntity(loc.clone().add(0, 0, 0), EntityType.SKELETON);
                axeSkeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                axeSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
                axeSkeleton.getEquipment().setHelmetDropChance(0f);
                axeSkeleton.getEquipment().setItemInMainHandDropChance(0f);
                axeSkeleton.setShouldBurnInDay(false);
                axeSkeleton.setTarget(necromancer.getTarget());

                // Skeleton 2: Sword + Shield
                Skeleton swordShieldSkeleton = (Skeleton) loc.getWorld().spawnEntity(loc.clone().add(0, 0, 0), EntityType.SKELETON);
                swordShieldSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                swordShieldSkeleton.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
                swordShieldSkeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                swordShieldSkeleton.getEquipment().setItemInMainHandDropChance(0f);
                swordShieldSkeleton.getEquipment().setItemInOffHandDropChance(0f);
                swordShieldSkeleton.getEquipment().setHelmetDropChance(0f);
                swordShieldSkeleton.setShouldBurnInDay(false);
                swordShieldSkeleton.setTarget(necromancer.getTarget());

                // Skeleton 3: Bow
                Skeleton scytheSkeleton = (Skeleton) loc.getWorld().spawnEntity(loc.clone().add(0, 0, 0), EntityType.SKELETON);
                scytheSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_HOE));
                scytheSkeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                scytheSkeleton.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
                scytheSkeleton.getEquipment().setItemInMainHandDropChance(0f);
                scytheSkeleton.getEquipment().setHelmetDropChance(0f);
                scytheSkeleton.setShouldBurnInDay(false);
                scytheSkeleton.setTarget(necromancer.getTarget());

                axeSkeleton.setCustomNameVisible(false);
                swordShieldSkeleton.setCustomNameVisible(false);
                scytheSkeleton.setCustomNameVisible(false);

                isSpawningBoss = false;
            }
        }.runTaskTimer(trolling, 0L, 20L * 10);
    }


    @EventHandler
    public void onNecromancerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton witherSkeleton)) return;
        if (witherSkeleton.hasMetadata("isBoss")) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta itemMeta = book.getItemMeta();
            NamespacedKey key = new NamespacedKey(trolling, "summoning_book");
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            itemMeta.displayName(Component.text("Necromancer's Summoning Tome", NamedTextColor.GRAY));
            itemMeta.lore(Collections.singletonList(Component.text("Right-click to summon skeletal minions that attack anything but you!", NamedTextColor.GRAY)));
            book.setItemMeta(itemMeta);
            event.getDrops().add(book);
        }
    }

    @EventHandler
    public void onPlayerUseSummoningBook(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;


        Player player = event.getPlayer();
        if (player.getCooldown(Material.ENCHANTED_BOOK) > 0) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(new NamespacedKey(trolling, "summoning_book"), PersistentDataType.BYTE))
            return;

        event.setCancelled(true);
        player.setCooldown(Material.ENCHANTED_BOOK, 20 * 180);
        summonMinions(player.getLocation(), player);
    }

    private void summonMinions(Location location, Player owner) {
        isSpawningBoss = true;
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 2);
        owner.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, owner.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
        owner.getWorld().spawnParticle(Particle.SCULK_SOUL, owner.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);
        for (int i = 0; i < 3; i++) {
            Skeleton minion = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
            minion.customName(Component.text(owner.getName() + "'s Minion", NamedTextColor.WHITE));
            minion.setCustomNameVisible(true);
            minion.setShouldBurnInDay(false);
            minion.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            minion.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
            minion.getEquipment().setHelmetDropChance(0f);
            minion.getEquipment().setItemInMainHandDropChance(0f);
            minion.setMetadata("isMinion", new FixedMetadataValue(trolling, true));

            minion.getPersistentDataContainer().set(new NamespacedKey(trolling, "owner_uuid"), PersistentDataType.STRING, owner.getUniqueId().toString());

            // Optional: make them stronger
            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 90, 1));

            startTargetingHostileMobs(minion, owner);


            // Despawn after 1 minute
            new BukkitRunnable() {
                @Override
                public void run() {
                    minion.getWorld().playSound(minion.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 0.3f);
                    owner.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, owner.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.01);
                    owner.getWorld().spawnParticle(Particle.SCULK_SOUL, owner.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.01);
                    if (!minion.isDead()) minion.remove();
                }
            }.runTaskLater(trolling, 20L * 60);
        }
        isSpawningBoss = false;
    }

    @EventHandler
    public void onMinionTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;
        if (!event.getEntity().hasMetadata("isMinion")) return;

        if (event.getTarget() instanceof Player) {
            event.setCancelled(true);
        }
    }

    private void startTargetingHostileMobs(Skeleton minion, Player owner) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!minion.isValid() || minion.isDead()) {
                    cancel();
                    return;
                }
                if (minion.getTarget() != null) return;

                List<Entity> nearby = minion.getNearbyEntities(10, 5, 10);
                for (Entity e : nearby) {
                    if (e instanceof LivingEntity && e != owner && !(e.hasMetadata("isMinion"))) {
                        minion.setTarget((LivingEntity) e);
                        break;
                    }
                }
            }
        }.runTaskTimer(trolling, 0L, 40L); // Every 2 seconds
    }
}


