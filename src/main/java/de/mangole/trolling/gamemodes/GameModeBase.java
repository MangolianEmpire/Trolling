package de.mangole.trolling.gamemodes;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.GameMode;
import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public abstract class GameModeBase implements Listener {

    protected final Trolling trolling;
    protected final GameMode gameMode;
    protected final GameManager gameManager;

    public GameModeBase(Trolling trolling, GameMode gameMode) {
        this.trolling = trolling;
        this.gameMode = gameMode;
        this.gameManager =trolling.getGameManager();

        registerAnnotatedEvents(trolling);
    }

    protected boolean isActive() {
        return trolling.getGameManager().getGameMode() == gameMode;
    }

    private void registerAnnotatedEvents(Plugin plugin) {
        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(GameModeEvent.class)) continue;

            // Muss genau 1 Argument haben, das ein Bukkit Event ist
            if (method.getParameterCount() != 1) continue;
            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            // Executor erzeugen
            EventExecutor executor = (listener, event) -> {
                if (!isActive()) return;
                if (paramType.isAssignableFrom(event.getClass())) {
                    try {
                        method.setAccessible(true);
                        method.invoke(this, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            // Listener registrieren
            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) paramType,
                    this,
                    org.bukkit.event.EventPriority.NORMAL,
                    executor,
                    plugin
            );
        }
    }
}
