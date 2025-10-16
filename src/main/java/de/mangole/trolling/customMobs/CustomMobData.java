package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.mobs.GravityCreeper;
import de.mangole.trolling.customMobs.mobs.ZombieMother;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class CustomMobData {

    private static final Map<String, Supplier<ICustomMob>> MOB_FACTORIES = new HashMap<>();
    private static final Map<UUID, ICustomMob> activeMobs = new HashMap<>();
    private final Trolling trolling;


    public CustomMobData(Trolling trolling) {
        this.trolling = trolling;

        registerMob("zombie_mother", () -> new ZombieMother(trolling));
        registerMob("gravity_creeper", () -> new GravityCreeper(trolling));
    }

    private void registerMob(String id, Supplier<ICustomMob> factory) {
        MOB_FACTORIES.put(id, factory);
    }

    public ICustomMob spawn(String id, Location location) {
        Supplier<ICustomMob> factory = MOB_FACTORIES.get(id);
        if (factory == null) throw new IllegalArgumentException("No CustomMob registered with ID: " + id);
        ICustomMob mob = factory.get();
        mob.spawn(location);
        activeMobs.put(mob.getMob().getUniqueId(), mob);
        return mob;
    }

    public void destroyAll() {
        for (ICustomMob mob : activeMobs.values()) {
            mob.destroy();
        }
        activeMobs.clear();
    }
}
