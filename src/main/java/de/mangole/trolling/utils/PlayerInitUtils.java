package de.mangole.trolling.utils;

import de.mangole.trolling.Data;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.WorldManager;
import de.mangole.trolling.gamemodes.GameModeFortnite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

public class PlayerInitUtils {

    private PlayerInitUtils() {}

    public static void initPlayerLobby(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, Data.lobbySpawnTeleporter);
        player.getInventory().setItem(4, Data.lobbySettings);
        player.getInventory().setItem(8, Data.lobbyGameModeChanger);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
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

        GameModeFortnite.fortniteSpawnPlayers.remove(player);
    }

    public static void initPlayerGame(Player player) {
        World gameOverWorld = Bukkit.getWorld("game_overworld");
        player.getInventory().clear();
        player.teleport(gameOverWorld.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
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

    public static void initPlayerFortnite(Player player) {
        World gameOverWorld = Bukkit.getWorld("game_overworld");
        player.getInventory().clear();
        Location skyspawn = gameOverWorld.getSpawnLocation().add(0, 150, 0);
        player.teleport(skyspawn);
        player.playSound(skyspawn, Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
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

        GameModeFortnite.fortniteSpawnPlayers.add(player);
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
        player.sendMessage(Component.text("You jumped out of the Battle Bus. Let's go!", NamedTextColor.BLUE));
        new BukkitRunnable() {
            public void run() {
                player.setGliding(true);
            }
        }.runTaskLater(Trolling.plugin, 5L);
    }
}
