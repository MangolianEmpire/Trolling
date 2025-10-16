package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class CustomMob implements ICustomMob, Listener {

    protected Trolling trolling;
    protected Class<? extends Mob> mobClass;
    protected Mob mob;
    protected String name;
    protected String id;
    protected boolean visibleName;
    protected double health;

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
            m.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
            m.setCustomName(name);
            m.setCustomNameVisible(visibleName);
        }, CreatureSpawnEvent.SpawnReason.CUSTOM);

        this.mob = newMob;
        registerCustomMobEvents();
    }

    private boolean destroyed = false;

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

                    Method getEntityMethod = null;
                    try {
                        getEntityMethod = event.getClass().getMethod("getEntity");
                    } catch (NoSuchMethodException ignored) {
                    }

                    // Wenn es eine Entity gibt, nur triggern, wenn sie dem CustomMob entspricht
                    if (getEntityMethod != null) {
                        Object entity = getEntityMethod.invoke(event);
                        if (!(entity instanceof Mob)) return;
                        if (!entity.equals(this.mob)) return;
                    }

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

    @CustomMobEvent
    private void onDeath(EntityDeathEvent event) {
        destroy();
    }

    @CustomMobEvent
    private void onRemove(EntityRemoveEvent event) {
        destroy();
    }
}
