package de.mangole.trolling.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.PacketType;
import de.mangole.trolling.Trolling;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChunkPacketUtils {

    private final ProtocolManager protocolManager;
    private final Trolling trolling;

    public ChunkPacketUtils(Trolling trolling) {
        this.trolling = trolling;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void sendUnloadChunk(Player player, int chunkX, int chunkZ) {
        try {
            PacketContainer unloadPacket = protocolManager.createPacket(PacketType.Play.Server.UNLOAD_CHUNK);
            unloadPacket.getChunkCoordIntPairs().write(0, new ChunkCoordIntPair(chunkX, chunkZ));
            protocolManager.sendServerPacket(player, unloadPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUnloadChunk(Player player, org.bukkit.Chunk chunk) {
        sendUnloadChunk(player, chunk.getX(), chunk.getZ());
    }
}
