package de.mangole.trolling.utils;

import de.mangole.trolling.Data;
import de.mangole.trolling.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class PlayerInitUtils {

    private PlayerInitUtils() {}

    public static void initPlayerLobby(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, Data.lobbySpawnTeleporter);
        player.getInventory().setItem(4, Data.lobbySettings);
        player.getInventory().setItem(8, Data.lobbyGameModeChanger);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExperienceLevelAndProgress(0);
        player.setInvisible(false);
        player.setCollidable(true);
        player.setAllowFlight(false);
        player.teleport(WorldManager.lobbySpawn);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
        player.clearActivePotionEffects();
        for (@NotNull Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement adv = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(adv);
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

    public static void initPlayerGame(Player player) {
        World gameOverWorld = Bukkit.getWorld("game_overworld");
        player.getInventory().clear();
        player.teleport(gameOverWorld.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExperienceLevelAndProgress(0);
        player.setInvisible(false);
        player.setCollidable(true);
        player.setAllowFlight(false);
        for (@NotNull Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement adv = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(adv);
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }
}
