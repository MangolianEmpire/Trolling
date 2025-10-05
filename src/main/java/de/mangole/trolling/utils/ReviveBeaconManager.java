package de.mangole.trolling.utils;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.gamemodes.GameModeFortnite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ReviveBeaconManager {

    private final Trolling trolling;
    private final File statusFile;
    private final YamlConfiguration config;

    public ReviveBeaconManager(Trolling trolling) {
        this.trolling = trolling;
        this.statusFile = new File(trolling.getDataFolder(), "revivebeacons.yml");
        this.config = YamlConfiguration.loadConfiguration(statusFile);
        loadBeacons();
    }

    public void loadBeacons() {
        if (!config.contains("beacons")) return;

        for (String key : config.getConfigurationSection("beacons").getKeys(false)) {
            String playerUUID = config.getString("beacons." + key + ".player");
            double x = config.getDouble("beacons." + key + ".x");
            double y = config.getDouble("beacons." + key + ".y");
            double z = config.getDouble("beacons." + key + ".z");
            String worldName = config.getString("beacons." + key + ".world");

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));


            if (Bukkit.getWorld(worldName) == null) continue;

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);

            GameModeFortnite.reviveBeacons.add(new ReviveBeacon(trolling, offlinePlayer, 5, 200, loc.getBlock()));
        }
    }

    public void saveBeacons() {
        config.set("beacons", null);

        int index = 0;
        for (ReviveBeacon beacon : new ArrayList<>(GameModeFortnite.reviveBeacons)) {
            String path = "beacons." + index;
            config.set(path + ".player", beacon.getOfflinePlayer().getUniqueId().toString());
            config.set(path + ".x", beacon.getReviveLocation().getX());
            config.set(path + ".y", beacon.getReviveLocation().getY());
            config.set(path + ".z", beacon.getReviveLocation().getZ());
            config.set(path + ".world", beacon.getReviveLocation().getWorld().getName());
            index++;
            beacon.cleanup();
        }

        try {
            config.save(statusFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

