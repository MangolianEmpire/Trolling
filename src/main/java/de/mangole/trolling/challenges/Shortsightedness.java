package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shortsightedness extends CustomChallenge {

    public Shortsightedness(Trolling trolling) {
        super(trolling, "Shortsightedness");
    }

    @Override
    protected void onActivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setViewDistance(2);
            player.setSendViewDistance(2);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
        }
    }

    @Override
    protected void onDeactivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setViewDistance(12);
            player.setSendViewDistance(12);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
    }

    @EventHandler
    public void onDrinkMilk(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
            player.clearActivePotionEffects();
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
        }
    }

}
