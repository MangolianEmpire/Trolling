package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customItems.items.Glasses;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shortsightedness extends CustomChallenge {

    private final Glasses glasses;

    public Shortsightedness(Trolling trolling) {
        super(trolling, "Shortsightedness");
        glasses = trolling.getCustomItemData().getGlasses();
    }

    @Override
    protected void onActivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
        }
        glasses.registerGlassesRecipe();
    }

    @Override
    protected void onDeactivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.clearActivePotionEffects();
        }
        glasses.removeGlassesRecipe();
    }

    @ChallengeEvent
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
    }

    @ChallengeEvent
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
    }

    @ChallengeEvent
    public void onDrinkMilk(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
            player.clearActivePotionEffects();
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
        }
    }

    @ChallengeEvent
    public void onGlassesEquip(PlayerInventorySlotChangeEvent event) {
        if (event.getSlot() != 39) return;

        ItemStack newItem = event.getNewItemStack();
        Player player = event.getPlayer();

        if (glasses.isCustomItem(newItem)) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            return;
        }
        if (newItem.getType() == Material.AIR) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
        }
    }
}
