package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class CustomMob implements Listener {

    protected Trolling trolling;
    protected Mob mob;
    protected String name;
    protected boolean visibleName;
    protected double health;

    public CustomMob(Trolling trolling, Mob mob) {
        this(trolling, mob, "", false);
    }

    public CustomMob(Trolling trolling, Mob mob, String name) {
        this(trolling, mob, name, false);
    }

    public CustomMob(Trolling trolling, Mob mob, String name, boolean visibleName) {
        this.trolling = trolling;
        this.mob = mob;
        this.name = name;
        this.visibleName = visibleName;
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
}
