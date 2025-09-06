package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FloodedWorld extends CustomChallenge {
    public FloodedWorld(Plugin plugin) {
        super(plugin, "Atlantis");
    }

    @Override
    protected void onActivate() {
        World world = Bukkit.getWorld("world");
        System.out.println(world);
        assert world != null;
        for (Chunk chunk : world.getLoadedChunks()) {
            world.unloadChunk(chunk);
            world.getChunkAt(chunk.getX(), chunk.getZ()).load(true);
        }
    }

    @Override
    protected void onDeactivate() {

    }

    private static final int FLOOD_Y_LEVEL = 96;

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL || world.getSeed() == -580394497689020177L) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = FLOOD_Y_LEVEL; y > world.getMinHeight(); y--) {
                            Block block = chunk.getBlock(x, y, z);
                            if (block.getType() == Material.AIR) {
                                block.setType(Material.WATER, false);
                            }
                        }
                    }
                }
            }
        }.runTask(plugin);
    }
}
