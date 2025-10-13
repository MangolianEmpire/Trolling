package de.mangole.trolling.customItems.items;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customItems.CustomArmorItem;
import de.mangole.trolling.customItems.CustomArmorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;

public class Glasses extends CustomArmorItem {

    public Glasses(Trolling trolling) {
        super(trolling, Component.text("Glasses", NamedTextColor.GRAY),
                Component.text("How much fingers do you see now?", NamedTextColor.GRAY), Material.TINTED_GLASS,
                1, false, CustomArmorType.HELMET);

        registerGlassesRecipe();
        Bukkit.getPluginManager().registerEvents(this, trolling);
    }

    public void registerGlassesRecipe() {
        NamespacedKey key = new NamespacedKey(trolling, "glasses_recipe");

        if (Bukkit.getRecipe(key) != null) {
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(key, getItemStack());
        recipe.shape("   ", "TST", "   ");
        recipe.setIngredient('T', Material.GLASS_PANE);
        recipe.setIngredient('S', Material.STICK);

        trolling.getServer().addRecipe(recipe);
    }

    public void removeGlassesRecipe() {
        NamespacedKey key = new NamespacedKey(trolling, "glasses_recipe");
        Bukkit.removeRecipe(key);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack glasses = inventory.getHelmet();
        if (glasses == null || glasses.getType().equals(Material.AIR)) return;
        if (!isCustomItem(glasses)) return;

        inventory.setHelmet(null);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
    }
}
