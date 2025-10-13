package de.mangole.trolling.customItems;

import de.mangole.trolling.Trolling;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;
import java.util.*;

public abstract class CustomItem implements Listener {

    protected Trolling trolling;
    protected Component name;
    protected Component description;
    protected Material material;
    protected int amount;
    protected boolean stackable;
    protected ItemStack item;

    public CustomItem(Trolling trolling, Component name, Component description, Material material) {
        this(trolling, name, description, material, 1);
    }

    public CustomItem(Trolling trolling, Component name, Component description, Material material, int amount) {
        this(trolling, name, description, material, amount, true);
    }

    public CustomItem(Trolling trolling, Component name, Component description, Material material, int amount, boolean stackable) {
        this.trolling = trolling;
        this.name = name;
        this.description = description;
        this.material = material;
        this.amount = amount;
        this.stackable = stackable;

        createItem();

        registerAnnotatedEvents(trolling);
    }

    private void createItem() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.lore(List.of(description));
        NamespacedKey itemKey = new NamespacedKey(trolling, "custom_item");
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, name.toString());

        item.setItemMeta(meta);
        this.item = item;
    }

    public boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey itemKey = new NamespacedKey(trolling, "custom_item");

        // Wenn der Key nicht existiert → kein CustomItem
        if (!meta.getPersistentDataContainer().has(itemKey, PersistentDataType.STRING)) {
            return false;
        }

        String storedName = meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        String thisName = this.name.toString();

        // Vergleich auf Basis des gespeicherten Strings
        return storedName != null && storedName.equals(thisName);
    }


    public ItemStack getItemStack() {
        ItemStack clone = item.clone();

        if (!stackable) {
            ItemMeta meta = clone.getItemMeta();
            NamespacedKey uniqueKey = new NamespacedKey(trolling, "unique_id");
            meta.getPersistentDataContainer().set(uniqueKey, PersistentDataType.STRING, UUID.randomUUID().toString());
            clone.setItemMeta(meta);
        }

        return clone;
    }


    protected void registerAnnotatedEvents(Trolling trolling) {
        Class<?> clazz = getClass();
        Set<Method> methods = new HashSet<>();

        while (clazz != null && clazz != Object.class) {
            Collections.addAll(methods, clazz.getDeclaredMethods());
            clazz = clazz.getSuperclass();
        }

        for (Method method : methods) {
            if (!method.isAnnotationPresent(CustomItemEvent.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            EventExecutor executor = (listener, event) -> {
                if (!paramType.isAssignableFrom(event.getClass())) return;

                ItemStack item = extractItemFromEvent(event);
                if (item == null || !isCustomItem(item)) return;

                try {
                    method.setAccessible(true);
                    method.invoke(this, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) paramType,
                    this,
                    org.bukkit.event.EventPriority.NORMAL,
                    executor,
                    trolling
            );
        }
    }


    private ItemStack extractItemFromEvent(Event event) {
        if (event instanceof org.bukkit.event.player.PlayerInteractEvent e)
            return e.getItem();

        if (event instanceof org.bukkit.event.player.PlayerItemConsumeEvent e)
            return e.getItem();

        if (event instanceof org.bukkit.event.player.PlayerItemHeldEvent e)
            return e.getPlayer().getInventory().getItem(e.getNewSlot());

        if (event instanceof org.bukkit.event.inventory.InventoryClickEvent e) {
            if (isCustomItem(e.getCursor()))
                return e.getCursor();
            if (isCustomItem(e.getCurrentItem()))
                return e.getCurrentItem();
        }

        if (event instanceof org.bukkit.event.entity.EntityDamageByEntityEvent e
                && e.getDamager() instanceof org.bukkit.entity.Player p)
            return p.getInventory().getItemInMainHand();

        if (event instanceof org.bukkit.event.player.PlayerDropItemEvent e)
            return e.getItemDrop().getItemStack();

        if (event instanceof org.bukkit.event.block.BlockPlaceEvent e)
            return e.getItemInHand();

        if (event instanceof CraftItemEvent e)
            return e.getRecipe().getResult();

        return null;
    }

    @CustomItemEvent
    protected void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @CustomItemEvent
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack result = event.getRecipe().getResult();

        // Wenn das Item nicht stapelbar ist
        if (!this.stackable) {
            event.setCancelled(true); // Vanilla-Crafting blocken, wir übernehmen selbst

            // Crafting-Inventar und Matrix
            var inv = event.getInventory();
            var matrix = inv.getMatrix();

            // Versuche das Rezept so oft zu craften, wie möglich
            int crafts = getMaxCraftableCount(matrix);

            for (int i = 0; i < crafts; i++) {
                ItemStack single = getItemStack().clone();
                single.setAmount(1);

                // Versuch, Item ins Inventar zu legen
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(single);

                // Wenn kein Platz mehr ist → abbrechen
                if (!leftover.isEmpty()) break;

                // Zutaten wie bei Vanilla-Crafting reduzieren
                for (int slot = 0; slot < matrix.length; slot++) {
                    ItemStack ingredient = matrix[slot];
                    if (ingredient == null || ingredient.getType() == Material.AIR) continue;
                    ingredient.setAmount(ingredient.getAmount() - 1);
                    if (ingredient.getAmount() <= 0) matrix[slot] = null;
                }
            }

            // Crafting-Matrix updaten
            inv.setMatrix(matrix);
            player.updateInventory();
        }
    }

    /**
     * Ermittelt, wie oft das aktuelle Rezept hergestellt werden kann (wie Vanilla beim Shift-Klick).
     */
    private int getMaxCraftableCount(ItemStack[] matrix) {
        int min = Integer.MAX_VALUE;
        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) continue;
            min = Math.min(min, item.getAmount());
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }
}
