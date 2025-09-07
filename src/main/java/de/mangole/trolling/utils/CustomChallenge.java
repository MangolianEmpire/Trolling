package de.mangole.trolling.utils;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class CustomChallenge implements Listener {

    private boolean active = false;
    protected final Trolling trolling;
    private final String challengeName;

    public CustomChallenge(Trolling trolling, String challengeName) {
        this.trolling = trolling;
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
            Bukkit.getPluginManager().registerEvents(this, trolling);
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

    // Von Unterklassen überschreiben
    protected abstract void onActivate();

    protected abstract void onDeactivate();
}
