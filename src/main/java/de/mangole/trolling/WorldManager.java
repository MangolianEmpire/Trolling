package de.mangole.trolling;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;

public class WorldManager {

    private final Plugin plugin;
    public static World lobbyWorld;
    public static Location lobbySpawn;
    public static Location portalLobbySpawn;

    public WorldManager(Plugin plugin) {
        this.plugin = plugin;
        loadWorlds();
        loadLocations();
    }

    private void loadWorlds() {
        WorldCreator creator = new WorldCreator("ChallengesLobby_world");
        World world = creator.createWorld();
        if (world != null) {
            world.setAutoSave(false);
        }
        lobbyWorld = world;

        WorldCreator wc1 = new WorldCreator("game_overworld").environment(World.Environment.NORMAL);
        Bukkit.createWorld(wc1);

        WorldCreator wc2 = new WorldCreator("game_nether").environment(World.Environment.NETHER);
        Bukkit.createWorld(wc2);

        WorldCreator wc3 = new WorldCreator("game_end").environment(World.Environment.THE_END);
        Bukkit.createWorld(wc3);

    }

    private void loadLocations() {
        lobbySpawn = new Location(lobbyWorld, 0.5, 31 ,-17.5, 0, 0);
        portalLobbySpawn = new Location(lobbyWorld, 0.5, 70 ,16.5, 0, 0);
        lobbyWorld.setSpawnLocation(lobbySpawn);
        lobbyWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }
}
