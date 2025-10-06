package de.mangole.trolling.challenges;

import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.events.GameChangeEvent;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DontTouch extends CustomChallenge {

    private final Material[] possibleBlocks = {
            Material.GRASS_BLOCK,
            Material.DIRT,
            Material.STONE,
            Material.SAND,
            Material.WATER,
            Material.NETHERRACK,
            Material.SOUL_SAND,
            Material.GRAVEL
    };
    private Material dontTouch = null;
    private Material[] oldSpawn = new Material[9];
    private boolean setup = false;

    public DontTouch(Trolling trolling) {
        super(trolling, "Dont touch that block");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
        dontTouch = null;
        if (setup) resetSpawn();
        setup = false;
    }

    @ChallengeEvent
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                showDeadlyBlock(event.getPlayer());
            }
        }.runTaskLater(trolling, 20L);
    }

    @ChallengeEvent
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        Material ground = event.getTo().clone().add(0, -0.1, 0).getBlock().getType();
        if (ground == dontTouch) {
            player.damage(player.getHealth());
        }
    }

    @ChallengeEvent(ignoreLobby = false)
    public void onGameStart(GameChangeEvent event) {
        if (event.getNewStatus() == GameStatus.RUNNING) {
            dontTouch = getRandomMaterial(possibleBlocks);
            if (!setup) {
                setupSpawn();
                setup = true;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        showDeadlyBlock(player);
                    }
                }
            }.runTaskLater(trolling, 20L);
        }
    }

    @ChallengeEvent(ignoreLobby = false)
    public void onGameStop(GameChangeEvent event) {
        if (event.getNewStatus() == GameStatus.LOBBY) {
            if (setup) {
                resetSpawn();
                setup = false;
            }
        }
    }

    private Material getRandomMaterial(Material[] materials) {
        Random random = new Random();
        return materials[random.nextInt(materials.length)];
    }

    private void showDeadlyBlock(Player player) {
        player.sendMessage(Component.text("Dont touch " + dontTouch.name(), NamedTextColor.RED));
        player.sendTitle(
                ChatColor.RED + dontTouch.name(),
                "",
                10,  // FadeIn
                40,  // Stay
                10   // FadeOut
        );
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.7f);
    }

    private void setupSpawn() {
        World overworld = Bukkit.getWorld("game_overworld");
        Location spawn = overworld.getSpawnLocation();

        int pos = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = spawn.clone().add(x, -1, z);
                oldSpawn[pos] = loc.getBlock().getType();
                loc.getBlock().setType(Material.BEDROCK);
                pos++;
            }
        }
    }

    private void resetSpawn() {
        World overworld = Bukkit.getWorld("game_overworld");
        Location spawn = overworld.getSpawnLocation();

        int pos = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = spawn.clone().add(x, -1, z);
                loc.getBlock().setType(oldSpawn[pos]);
                pos++;
            }
        }
    }
}
