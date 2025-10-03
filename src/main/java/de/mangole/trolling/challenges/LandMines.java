package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class LandMines extends CustomChallenge {
    private final Random random = new Random();
    private final Set<Material> mineMaterials = new HashSet<>();
    private final Map<UUID, Long> playerCooldowns = new HashMap<>();

    public LandMines(Trolling trolling) {
        super(trolling, "LandMines");
        mineMaterials.add(Material.SAND);
        mineMaterials.add(Material.RED_SAND);
        mineMaterials.add(Material.DIRT);
        mineMaterials.add(Material.COARSE_DIRT);
        mineMaterials.add(Material.GRASS_BLOCK);
        mineMaterials.add(Material.PODZOL);
        mineMaterials.add(Material.MYCELIUM);
        mineMaterials.add(Material.ROOTED_DIRT);
        mineMaterials.add(Material.GRAVEL);
        mineMaterials.add(Material.MUD);
        mineMaterials.add(Material.FARMLAND);
        mineMaterials.add(Material.CRIMSON_NYLIUM);
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @ChallengeEvent
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        World world = to.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        Location underLoc = to.clone().subtract(0, 1, 0);
        Block underBlock = underLoc.getBlock();
        if (!mineMaterials.contains(underBlock.getType())) {
            return;
        }
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (playerCooldowns.containsKey(playerId) && currentTime - playerCooldowns.get(playerId) < 500) {
            return;
        }
        playerCooldowns.put(playerId, currentTime);
        if (random.nextDouble() < 0.0025) {
            world.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, underLoc, 50, 0.5, 0.5, 0.5, 0.1);
            world.playSound(underLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            world.createExplosion(player.getLocation(), 6.0f, true, true);
        }
    }
}