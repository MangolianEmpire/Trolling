package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ChorusInfection extends CustomChallenge {

    public ChorusInfection(Trolling trolling) {
        super(trolling, "ChorusInfection");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType().equals(Material.CHORUS_FRUIT)) return;

        List<Location> possibleLocations = new ArrayList<>();
        Location currentLocation = player.getLocation();

        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                for (int y = -8; y <= 8; y++) {
                    Location teleportLocation = currentLocation.clone().add(x, y, z);
                    Block floor = teleportLocation.clone().add(0, -1, 0).getBlock();
                    Material feet = teleportLocation.getBlock().getType();
                    Material head = teleportLocation.clone().add(0, 1,0).getBlock().getType();

                    if (!(floor.isSolid() || floor.getType().equals(Material.WATER))) continue;
                    if (!(feet.equals(Material.AIR) || feet.equals(Material.WATER))) continue;
                    if (!(head.equals(Material.AIR) || head.equals(Material.WATER))) continue;

                    possibleLocations.add(teleportLocation);
                }
            }
        }

        if (possibleLocations.isEmpty()) return;

        Random rand = new Random();
        int teleportLocationIndex = rand.nextInt(possibleLocations.size());
        Location teleportLocation = possibleLocations.get(teleportLocationIndex);
        teleportLocation.setYaw(currentLocation.getYaw());
        teleportLocation.setPitch(currentLocation.getPitch());

        player.teleport(teleportLocation);
        player.getWorld().playSound(teleportLocation, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.PORTAL, teleportLocation.add(0,1,0), 20);
    }
}
