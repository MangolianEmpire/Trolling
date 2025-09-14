package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

        // Mother Slime
        if (mob instanceof Slime && random.nextDouble() <= 0.08) {
            event.setCancelled(true);
            isSpawningBoss = true;
            spawnSlimeBoss(mob.getLocation());
            isSpawningBoss = false;
        }
    }


    // Mother Slime
    private void spawnSlimeBoss(Location loc) {
        World world = loc.getWorld();
        Slime bossSlime = (Slime) world.spawnEntity(loc, EntityType.SLIME);
        bossSlime.setSize(10);
        bossSlime.customName(Component.text("§aMother Slime"));
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
        miniSlime.customName(Component.text("§aBaby Slime"));
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
}
