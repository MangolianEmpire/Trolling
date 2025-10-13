package de.mangole.trolling.customItems;

import de.mangole.trolling.Trolling;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class CustomArmorItem extends CustomItem {

    protected CustomArmorType customArmorType;

    public CustomArmorItem(Trolling trolling, Component name, Component description, Material material, CustomArmorType customArmorType) {
        this(trolling, name, description, material, 1, customArmorType);
    }

    public CustomArmorItem(Trolling trolling, Component name, Component description, Material material, int amount, CustomArmorType customArmorType) {
        this(trolling, name, description, material, amount, false, customArmorType);
    }

    public CustomArmorItem(Trolling trolling, Component name, Component description, Material material, int amount, boolean stackable, CustomArmorType customArmorType) {
        super(trolling, name, description, material, amount, stackable);
        this.customArmorType = customArmorType;
    }

    public CustomArmorType getCustomArmorType() {
        return customArmorType;
    }

    @CustomItemEvent
    protected void onRightClickEquip(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!event.getAction().isRightClick()) return;

        ItemStack item = event.getItem();

        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();

        switch (customArmorType) {
            case HELMET -> {
                if (inv.getHelmet() == null || inv.getHelmet().getType() == Material.AIR) {
                    inv.setHelmet(item.clone().asQuantity(1));
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
            case CHESTPLATE -> {
                if (inv.getChestplate() == null || inv.getChestplate().getType() == Material.AIR) {
                    inv.setChestplate(item.clone().asQuantity(1));
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
            case LEGGINGS -> {
                if (inv.getLeggings() == null || inv.getLeggings().getType() == Material.AIR) {
                    inv.setLeggings(item.clone().asQuantity(1));
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
            case BOOTS -> {
                if (inv.getBoots() == null || inv.getBoots().getType() == Material.AIR) {
                    inv.setBoots(item.clone().asQuantity(1));
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
        }
    }

    @CustomItemEvent
    protected void onInventoryEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.PLAYER)
            return;
        if (event.getAction() == InventoryAction.NOTHING) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        int slot = event.getSlot();

        int targetSlot = switch (customArmorType) {
            case HELMET -> 39;
            case CHESTPLATE -> 38;
            case LEGGINGS -> 37;
            case BOOTS -> 36;
        };

        // === FALL 1: SHIFT-KLICK aus normalem Inventar ===
        if (event.isShiftClick()) {
            if (current == null || current.getType() == Material.AIR) return;
            if (!isCustomItem(current)) return;

            ItemStack targetItem = inventory.getItem(targetSlot);

            // Slot leer → automatisch anlegen
            if (targetItem == null || targetItem.getType() == Material.AIR) {
                inventory.setItem(targetSlot, current.clone().asQuantity(1));
                current.setAmount(current.clone().getAmount() - 1);
                event.setCancelled(true);
                player.updateInventory();
            }
            return;
        }

        // === FALL 2: Direkter Klick auf den spezifischen Armor-Slot ===
        if (slot == targetSlot) {
            // Aufsetzen
            if (isCustomItem(cursor)) {
                ItemStack oldItem = inventory.getItem(targetSlot);

                // Wenn dort bereits das gleiche CustomItem ausgerüstet ist → nichts machen
                if (oldItem != null && isCustomItem(oldItem) && getCustomName(oldItem).equals(getCustomName(cursor))) {
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }

                // Ausrüsten
                inventory.setItem(targetSlot, cursor.clone().asQuantity(1));
                cursor.setAmount(cursor.getAmount() - 1);

                // Tauschen falls vorher was dort war
                if (oldItem != null && oldItem.getType() != Material.AIR) {
                    player.setItemOnCursor(oldItem);
                }

                event.setCancelled(true);
                player.updateInventory();
                return;
            }

            // Abnehmen
            if (cursor.getType() == Material.AIR) {
                ItemStack equipped = inventory.getItem(targetSlot);
                if (equipped != null && isCustomItem(equipped)) {
                    inventory.setItem(targetSlot, null);
                    player.setItemOnCursor(equipped);
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }


    private String getCustomName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "";
        return item.getItemMeta().displayName().toString();
    }
}
