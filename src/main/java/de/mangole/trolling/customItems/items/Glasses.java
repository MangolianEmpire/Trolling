package de.mangole.trolling.customItems.items;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customItems.CustomItem;
import de.mangole.trolling.customItems.CustomItemEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

public class Glasses extends CustomItem {

    public Glasses(Trolling trolling) {
        super(trolling, Component.text("Glasses", NamedTextColor.GRAY),
                Component.text("How much fingers do you see now?", NamedTextColor.GRAY), Material.TINTED_GLASS);

        registerGlassesRecipe();
    }

    public void registerGlassesRecipe() {
        NamespacedKey key = new NamespacedKey(trolling, "glasses_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, getItemStack());

        // Crafting-Pattern
        recipe.shape("   ", "TST", "   ");

        // Zutaten setzen
        recipe.setIngredient('T', Material.GLASS_PANE);
        recipe.setIngredient('S', Material.STICK);

        // Rezept registrieren
        trolling.getServer().addRecipe(recipe);
    }

    private void removeGlassesRecipe() {
        NamespacedKey key = new NamespacedKey(trolling, "glasses_recipe");
        Bukkit.removeRecipe(key);
    }

    @CustomItemEvent
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        ItemStack item = event.getItem();

        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        if (inv.getHelmet() == null || inv.getHelmet().getType() == Material.AIR) {
            inv.setHelmet(item.clone().asQuantity(1)); // Item auf den Kopf
            item.setAmount(item.getAmount() - 1); // aus Hand entfernen
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new PlayerInventorySlotChangeEvent(player, 39, inv.getHelmet(), item));
        }
    }

    @CustomItemEvent
    public void onInventoryEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getClickedInventory() instanceof PlayerInventory inventory)) return;

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        int slot = event.getSlot();

        // Prüfen, ob Helm-Slot betroffen ist (Slot 39 in Bukkit)
        boolean isHelmetSlot = slot == 39;

        // --- FALL 1: SHIFT-KLICK aus normalem Inventar ---
        if (event.isShiftClick()) {
            ItemStack shiftItem = current;
            if (shiftItem == null || shiftItem.getType() == Material.AIR) return;

            ItemStack helmet = inventory.getHelmet();
            if (helmet == null || helmet.getType() == Material.AIR) {
                // Helm-Slot ist leer → automatisch anlegen
                inventory.setHelmet(shiftItem.clone());
                shiftItem.setAmount(0);
                event.setCancelled(true);
                player.updateInventory();
            } else {
                // Helm-Slot belegt → tauschen
                inventory.setItem(event.getSlot(), helmet.clone());
                inventory.setHelmet(shiftItem.clone());
                event.setCancelled(true);
                player.updateInventory();
            }
            return;
        }

        // --- FALL 2: Direkter Klick auf den Helm-Slot ---
        if (isHelmetSlot) {
            // Der Spieler klickt direkt auf den Helm-Slot
            if (cursor != null && isCustomItem(cursor)) {
                // Spieler hält unsere Brille am Cursor → aufsetzen
                ItemStack oldHelmet = inventory.getHelmet();

                // Helm tauschen
                inventory.setHelmet(cursor.clone());
                cursor.setAmount(0);

                if (oldHelmet != null && oldHelmet.getType() != Material.AIR) {
                    player.setItemOnCursor(oldHelmet);
                }

                event.setCancelled(true);
                player.updateInventory();
            } else if (cursor == null || cursor.getType() == Material.AIR) {
                // Cursor leer → Spieler will den Helm abnehmen
                ItemStack oldHelmet = inventory.getHelmet();
                if (oldHelmet != null && isCustomItem(oldHelmet)) {
                    inventory.setHelmet(null);
                    player.setItemOnCursor(oldHelmet);
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

}
