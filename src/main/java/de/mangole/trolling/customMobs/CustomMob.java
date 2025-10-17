package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class CustomMob implements ICustomMob, Listener {

    protected final Trolling trolling;
    protected final Class<? extends Mob> mobClass;
    protected Mob mob;
    protected final String name;
    protected final String id;
    protected final boolean visibleName;
    protected final double health;
    private boolean destroyed = false;
    private UUID uniqueMobId; // eindeutige ID pro Mob
    private static final String PDC_KEY = "custom_mob_id";

    public CustomMob(Trolling trolling, String id, Class<? extends Mob> mobClass) {
        this(trolling, id, mobClass, "");
    }

    public CustomMob(Trolling trolling, String id, Class<? extends Mob> mobClass, String name) {
        this(trolling, id, mobClass, name, false);
    }

    public CustomMob(Trolling trolling, String id, Class<? extends Mob> mobClass, String name, boolean visibleName) {
        this(trolling, id, mobClass, name, visibleName, -1);
    }

    public CustomMob(Trolling trolling, String id, Class<? extends Mob> mobClass, String name, boolean visibleName, double health) {
        this.trolling = trolling;
        this.id = id;
        this.mobClass = mobClass;
        this.name = name;
        this.visibleName = visibleName;
        this.health = health;
    }

    @Override
    public void spawn(Location location) {
        Mob newMob = location.getWorld().spawn(location, mobClass, (m) -> {
            if (health > 0) {
                m.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
                m.setHealth(health);
            }
            if (!name.isEmpty()) {
                m.setCustomName(name);
                m.setCustomNameVisible(visibleName);
            }
        }, CreatureSpawnEvent.SpawnReason.CUSTOM);

        // Eindeutige ID generieren und speichern
        this.uniqueMobId = UUID.randomUUID();
        NamespacedKey key = new NamespacedKey(trolling, PDC_KEY);
        PersistentDataContainer data = newMob.getPersistentDataContainer();
        data.set(key, PersistentDataType.STRING, uniqueMobId.toString());

        this.mob = newMob;
        registerCustomMobEvents();
    }

    @Override
    public void destroy() {
        if (destroyed) return;
        destroyed = true;

        HandlerList.unregisterAll(this);
        if (mob != null && !mob.isDead()) mob.remove();
    }

    @Override
    public Mob getMob() {
        return mob;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public UUID getUniqueMobId() {
        return uniqueMobId;
    }

    public void setMob(Mob existingMob) {
        this.mob = existingMob;

        registerCustomMobEvents();
    }

    /**
     * Prüft, ob eine Entity zum aktuellen CustomMob gehört.
     */
    private boolean isThisCustomMob(Object entity) {
        if (!(entity instanceof Mob mobEntity)) return false;
        NamespacedKey key = new NamespacedKey(trolling, PDC_KEY);
        PersistentDataContainer data = mobEntity.getPersistentDataContainer();
        String storedId = data.get(key, PersistentDataType.STRING);
        return storedId != null && storedId.equals(uniqueMobId.toString());
    }

    /**
     * Registriert alle Methoden mit @CustomMobEvent und führt sie nur aus,
     * wenn das Event mit *diesem* CustomMob verknüpft ist (über PDC-ID).
     */
    protected void registerCustomMobEvents() {
        Class<?> clazz = getClass();
        Set<Method> methods = new HashSet<>();

        while (clazz != null && clazz != Object.class) {
            Collections.addAll(methods, clazz.getDeclaredMethods());
            clazz = clazz.getSuperclass();
        }

        for (Method method : methods) {
            if (!method.isAnnotationPresent(CustomMobEvent.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            EventExecutor executor = (listener, event) -> {
                if (!paramType.isAssignableFrom(event.getClass())) return;

                try {
                    boolean relevant = false;

                    // 1️⃣ Ziel prüfen
                    Object entity = invokeMethodIfExists(event, "getEntity");
                    if (isThisCustomMob(entity)) relevant = true;

                    // 2️⃣ Angreifer prüfen
                    if (!relevant) {
                        Object damager = invokeMethodIfExists(event, "getDamager");
                        if (isThisCustomMob(damager)) relevant = true;
                    }

                    // 3️⃣ Projektil-Shooter prüfen
                    if (!relevant) {
                        Object damager = invokeMethodIfExists(event, "getDamager");
                        if (damager instanceof Projectile projectile) {
                            Object shooter = projectile.getShooter();
                            if (isThisCustomMob(shooter)) relevant = true;
                        }
                    }

                    if (!relevant) return;

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

    private Object invokeMethodIfExists(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    @CustomMobEvent
    private void onDeath(EntityDeathEvent event) {
        destroy();
    }

    @CustomMobEvent
    private void onRemove(EntityRemoveEvent event) {
        destroy();
    }
}
