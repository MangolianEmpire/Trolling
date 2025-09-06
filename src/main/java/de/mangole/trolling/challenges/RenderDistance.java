package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import de.mangole.trolling.utils.ChunkPacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class RenderDistance extends CustomChallenge {

    private final Map<Player, Chunk> currentChunks = new HashMap<>();
    private final ChunkPacketUtils chunkPacketUtils;

    public RenderDistance(Trolling trolling) {
        super(trolling, "RenderDistance");
        chunkPacketUtils = new ChunkPacketUtils(trolling);
    }

    @Override
    protected void onActivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setupPlayer(player);
        }
    }

    @Override
    protected void onDeactivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Spieler wieder normale Sichtweite geben
            player.setViewDistance(12);
            player.setSendViewDistance(12);

            // Alle Chunks im Radius neu laden
            Chunk current = currentChunks.get(player);
            if (current != null) {
                int radius = 12; // normale Sichtweite
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        player.getWorld().getChunkAt(current.getX() + dx, current.getZ() + dz).load();
                    }
                }
            }
        }
        currentChunks.clear();
    }

    private void setupPlayer(Player player) {
        Chunk current = player.getLocation().getChunk();
        currentChunks.put(player, current);

        // Sichtweite reduzieren
        player.setViewDistance(2);
        player.setSendViewDistance(2);

        // Andere Chunks auf Void setzen
        Bukkit.getScheduler().runTaskLater(trolling, () -> clearOtherChunks(player, current), 2);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setupPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;

        Chunk from = event.getFrom().getChunk();
        Chunk to = event.getTo().getChunk();
        Player player = event.getPlayer();

        if (!from.equals(to))  {
            // Alten Chunk auf Void setzen
            chunkPacketUtils.sendUnloadChunk(player, from);

            // Neuen Chunk als aktuell markieren
            currentChunks.put(player, to);

            // Sicherstellen, dass neuer Chunk geladen ist
            Chunk newChunk = player.getWorld().getChunkAt(to.getX(), to.getZ());
            if (!newChunk.isLoaded()) newChunk.load();

            // Alle anderen Chunks außerhalb des neuen Chunks auf Void setzen
            Bukkit.getScheduler().runTask(trolling, () -> clearOtherChunks(player, newChunk));
        } else {
            from.load();
        }
    }

    private void clearOtherChunks(Player player, Chunk newChunk) {
        if (newChunk == null) return;

        int radius = Bukkit.getServer().getViewDistance();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int x = newChunk.getX() + dx;
                int z = newChunk.getZ() + dz;
                Chunk chunk = player.getWorld().getChunkAt(x, z);

                if (chunk.equals(newChunk)) {
                    player.sendMessage("chunk laden");
                    chunk.load();
                } else {
                    chunkPacketUtils.sendUnloadChunk(player, chunk);
                    player.sendMessage("chunk entladen");
                }
            }
        }
    }
}
