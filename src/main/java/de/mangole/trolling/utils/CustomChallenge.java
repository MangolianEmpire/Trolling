package de.mangole.trolling.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class CustomChallenge implements Listener {

    private boolean active = false;
    protected final Plugin plugin;
    private final String challengeName;

    public CustomChallenge(Plugin plugin, String challengeName) {
        this.plugin = plugin;
        this.challengeName = challengeName;
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
            Bukkit.getPluginManager().registerEvents(this, plugin);
            onActivate();
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage("§aChallenge §5" + challengeName + "§a wurde aktiviert");
            });
        }
    }

    public void deactivate() {
        if (active) {
            active = false;
            HandlerList.unregisterAll(this);
            onDeactivate();
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage("§cChallenge §5" + challengeName + " §cwurde deaktiviert");
            });
        }
    }

    public void loseChallenge() {

    }

    // Von Unterklassen überschreiben
    protected abstract void onActivate();

    protected abstract void onDeactivate();
}
