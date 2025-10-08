package de.mangole.trolling.utils;

import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.events.GameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class Deathcounter implements Listener {

    private final Trolling trolling;
    private HashMap<UUID, Integer> deaths = new HashMap<>();

    public Deathcounter(Trolling trolling) {
        this.trolling = trolling;
        Bukkit.getPluginManager().registerEvents(this, trolling);
    }

    public void initHashmap(UUID uuid, int death_count) {
        deaths.put(uuid, death_count);
    }

    public HashMap<UUID, Integer> getDeaths() {
        return deaths;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(trolling, this::updateTabForAll, 2L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        deaths.put(player.getUniqueId(), deaths.getOrDefault(player.getUniqueId(), 0) + 1);
        Bukkit.getScheduler().runTaskLater(trolling, this::updateTabForAll, 2L);
    }

    @EventHandler
    public void onRestart(GameChangeEvent event) {
        if (event.getNewStatus() == GameStatus.RUNNING && event.getOldStatus() == GameStatus.LOBBY) {
            deaths.clear();
            updateTabForAll();
        }
    }

    private void updateTabForAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int death_count = deaths.getOrDefault(player.getUniqueId(), 0);
            Component text = Component.text(player.getName(), NamedTextColor.WHITE).
                    append(Component.text(" [" + death_count + "]", NamedTextColor.GRAY));
            player.playerListName(text);
        }
    }
}
