package de.mangole.trolling.utils;

import de.mangole.trolling.Trolling;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class DeathcounterManager {

    private final Trolling trolling;
    private final File statusFile;
    private final YamlConfiguration config;
    private final Deathcounter deathcounter;

    public DeathcounterManager(Trolling trolling) {
        this.trolling = trolling;
        this.statusFile = new File(trolling.getDataFolder(), "deathcounter.yml");

        if (!statusFile.exists()) {
            try {
                statusFile.getParentFile().mkdirs();
                statusFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(statusFile);
        this.deathcounter = new Deathcounter(trolling);
        loadDeaths();
    }

    private void loadDeaths() {
        if (!config.contains("deaths")) return;

        var section = config.getConfigurationSection("deaths");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int deaths = config.getInt("deaths." + key, 0);
                deathcounter.initHashmap(uuid, deaths);
            } catch (IllegalArgumentException e) {
                trolling.getLogger().warning("Invalid UUID in deathcounter.yml: " + key);
            }
        }
    }

    public void saveDeaths() {
        for (Map.Entry<UUID, Integer> entry : deathcounter.getDeaths().entrySet()) {
            UUID uuid = entry.getKey();
            int deaths = entry.getValue();
            config.set("deaths." + uuid.toString(), deaths);
        }

        try {
            config.save(statusFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Deathcounter getDeathcounter() {
        return deathcounter;
    }
}
