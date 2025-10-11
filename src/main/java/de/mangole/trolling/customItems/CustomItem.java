package de.mangole.trolling.customItems;

import de.mangole.trolling.Trolling;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

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

        if (!stackable) {
            NamespacedKey uniqueKey = new NamespacedKey(trolling, "unique_id");
            meta.getPersistentDataContainer().set(uniqueKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        }

        item.setItemMeta(meta);
        this.item = item;
    }

    public ItemStack getItemStack() {
        return item;
    }


    private void registerAnnotatedEvents(Trolling trolling) {
        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CustomItemEvent.class)) continue;

            if (method.getParameterCount() != 1) continue;
            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            EventExecutor executor = (listener, event) -> {
                if (paramType.isAssignableFrom(event.getClass())) {
                    try {
                        method.setAccessible(true);
                        method.invoke(this, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
}
