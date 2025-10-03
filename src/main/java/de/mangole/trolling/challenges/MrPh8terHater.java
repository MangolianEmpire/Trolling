package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

public class MrPh8terHater extends CustomChallenge {

    private static final String VICTIM_NAME = "Xander_008";


    public MrPh8terHater(Trolling trolling) {
        super(trolling, "MrPh8terHater");
    }

    @Override
    protected void onActivate() {

    }

    @Override
    protected void onDeactivate() {

    }

    // Takes more damage
    @EventHandler
    public void onEntitydamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getName().equals(VICTIM_NAME)) {
                double newDamage = event.getDamage() * 1.2;
                event.setDamage(newDamage);
            }
        }
    }

    // Degrades tools faster when breaking blocks
    @EventHandler
    public void onPlayerUseTool(BlockBreakEvent event) {
        if (new Random().nextDouble() > 0.3) return;
        degradeToolInHand(event.getPlayer());
    }

    // Deals less damage and degrade weapons faster
    @EventHandler
    public void onEntitydamage(EntityDamageByEntityEvent event) {
        if (new Random().nextDouble() > 0.3) return;
        if (event.getDamager() instanceof Player damager) {

            if (damager.getName().equals(VICTIM_NAME)) {
                double originalDamage = event.getDamage();
                double reducedDamage = originalDamage * 0.8;
                event.setDamage(reducedDamage);
                degradeToolInHand(damager);
            }
        }
    }

    private void degradeToolInHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (player.getName().equals(VICTIM_NAME) && isTool(item.getType())) {
            Damageable damageableMeta = (Damageable) item.getItemMeta();
            damageableMeta.setDamage(damageableMeta.getDamage() + 1);
            item.setItemMeta(damageableMeta);
        }
    }

    private boolean isTool(Material material) {
        return material.toString().endsWith("_SWORD")
                    || material.toString().endsWith("_PICKAXE")
                    || material.toString().endsWith("_AXE")
                    || material.toString().endsWith("_SHOVEL")
                    || material.toString().endsWith("_HOE")
                    || material.toString().endsWith("_SHEARS");
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        if (event.getLightning().hasMetadata("customStrike")) return; // ignore custom strikes

        if (new Random().nextDouble() > 0.0) return;

        Player target = Bukkit.getPlayerExact(VICTIM_NAME);
        if (target != null && target.getWorld().equals(event.getWorld())) {
            event.setCancelled(true);
            strikeCustomLightning(target.getWorld(), target.getLocation());
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getName().equalsIgnoreCase(VICTIM_NAME)) {
            event.setAmount(event.getAmount() * 0.8);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event){
        if (new Random().nextDouble() > 0.2) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!player.getName().equalsIgnoreCase(VICTIM_NAME)) return;

        int oldLevel = player.getFoodLevel();
        int newLevel = event.getFoodLevel();

        if (newLevel < oldLevel) {
            event.setFoodLevel(oldLevel + ((newLevel - oldLevel) * 2));
        }
    }

    private void strikeCustomLightning(World world, Location loc) {
        LightningStrike lightning = world.strikeLightning(loc);
        lightning.setMetadata("customStrike", new FixedMetadataValue(trolling, true));
    }

}
