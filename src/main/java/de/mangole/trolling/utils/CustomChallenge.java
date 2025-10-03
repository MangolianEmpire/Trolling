package de.mangole.trolling.utils;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public abstract class CustomChallenge implements Listener {

    private boolean active = false;
    protected final Trolling trolling;
    private final String challengeName;

    public CustomChallenge(Trolling trolling, String challengeName) {
        this.trolling = trolling;
        this.challengeName = challengeName;
        registerAnnotatedEvents(trolling);
    }

    public String getChallengeName() {
        return challengeName;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        if (!active) {
            active = true;
            onActivate();
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage("§aChallenge §5" + challengeName + "§a wurde aktiviert");
            });
        }
    }

    public void deactivate() {
        if (active) {
            active = false;
            onDeactivate();
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage("§cChallenge §5" + challengeName + " §cwurde deaktiviert");
            });
        }
    }

    // Von Unterklassen überschreiben
    protected abstract void onActivate();

    protected abstract void onDeactivate();

    private void registerAnnotatedEvents(Trolling trolling) {
        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ChallengeEvent.class)) continue;

            if (method.getParameterCount() != 1) continue;
            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            ChallengeEvent annotation = method.getAnnotation(ChallengeEvent.class);

            EventExecutor executor = (listener, event) -> {
                if (!isActive()) return; // nur aktiv, wenn Challenge läuft

                // LobbyWorld ignorieren, falls gesetzt
                World world = null;
                try {
                    // Event hat häufig eine getPlayer/getEntity Methode
                    if (event.getClass().getMethod("getPlayer") != null) {
                        world = ((org.bukkit.entity.Player) event.getClass().getMethod("getPlayer").invoke(event)).getWorld();
                    } else if (event.getClass().getMethod("getEntity") != null) {
                        world = ((org.bukkit.entity.Entity) event.getClass().getMethod("getEntity").invoke(event)).getWorld();
                    }
                } catch (Exception ignored) {}

                if (annotation.ignoreLobby() && world != null && world.getName().equalsIgnoreCase("ChallengesLobby_world")) return;

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
