package de.mangole.trolling.customMobs;

import org.bukkit.Location;
import org.bukkit.entity.Mob;

public interface ICustomMob {

    String getId();
    String getName();
    void spawn(Location location);
    void destroy();
    Mob getMob();

    void setMob(Mob mobEntity);
}
