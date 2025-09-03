package de.mangole.trolling.gamemodes;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public interface GameModeBase {
    void onDeath(PlayerDeathEvent event);

    void onRespawn(PlayerRespawnEvent event);
}
