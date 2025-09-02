package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.ContainerUtils;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Random;

public class WeirdChests extends CustomChallenge {

    public WeirdChests(Plugin plugin) {
        super(plugin, "WeirdChests");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (!isActive()) return;
        if (event.getClickedBlock() == null) return;
        Block chest = event.getClickedBlock();
        World world = chest.getWorld();

        if (chest.getType() != Material.CHEST) return;
        if (!event.getAction().isRightClick()) return;

        double random = new Random().nextDouble();

        if (random < 0.1) {
            event.setCancelled(true);
            world.createExplosion(chest.getLocation(), 10.0f);
        } else if (random < 0.2) {
            event.setCancelled(true);
            ArrayList<Block> surroundingBlocks = new ArrayList<>();

            // TODO: should be reworked
            for (int x = chest.getX() - 2; x <= chest.getX() + 2; x++) {
                for (int z = chest.getZ() - 2; z <= chest.getZ() + 2; z++) {
                    for (int y = chest.getY() - 2; y <= chest.getY() + 2; y++) {
                        if (x != 0 && z != 0 && y != 0) {
                            surroundingBlocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }

            world.spawnParticle(Particle.PORTAL, chest.getLocation(), 5);
            world.playSound(chest.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            int randomBlock = new Random().nextInt(surroundingBlocks.size());
            ContainerUtils.swapBlockContainers(chest, surroundingBlocks.get(randomBlock), plugin);
        }
    }


}
