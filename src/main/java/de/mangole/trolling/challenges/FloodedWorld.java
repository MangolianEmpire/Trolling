package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.WorldManager;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FloodedWorld extends CustomChallenge {
    public FloodedWorld(Trolling trolling) {
        super(trolling, "Atlantis");
    }

    private static final int FLOOD_Y_LEVEL = 96;

    @Override
    protected void onActivate() {
        World world = trolling.getServer().getWorld("game_overworld");
        assert world != null;
        Chunk[] chunks = world.getLoadedChunks();
        for (Chunk chunk : chunks) {
            floodChunk(chunk, world);
        }
    }

    @Override
    protected void onDeactivate() {

    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();

        if (world.getEnvironment() != World.Environment.NORMAL || WorldManager.lobbyWorld == world || !event.isNewChunk()) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                floodChunk(chunk, world);
            }
        }.runTask(trolling);
    }

    private static void floodChunk(Chunk chunk, World world) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = FLOOD_Y_LEVEL; y > world.getMinHeight(); y--) {
                    Block block = chunk.getBlock(x, y, z);
                    if (!block.isSolid()) {
                        block.setType(Material.WATER, false);
                    }
                }
            }
        }
    }
}
