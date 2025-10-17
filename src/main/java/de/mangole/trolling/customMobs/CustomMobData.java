package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.mobs.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;

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
        registerMob("machine_gun_skeleton", () -> new MachineGunSkeleton(trolling));
        registerMob("swapping_man", () -> new SwappingMan(trolling));
        registerMob("deadly_spider", () -> new DeadlySpider(trolling));
        registerMob("exploding_chicken", () -> new ExplodingChicken(trolling));
        registerMob("scared_cow", () -> new ScaredCow(trolling));
        registerMob("protector_golem", () -> new ProtectorGolem(trolling));
        registerMob("stealing_pig", () -> new StealingPig(trolling));
        registerMob("tnt_sheep", () -> new TNTSheep(trolling));
        registerMob("bat_drone", () -> new BatDrone(trolling));
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

    public void restoreMobsFromWorlds() {
        NamespacedKey key = new NamespacedKey(trolling, "custom_mob_id");

        for (World world : Bukkit.getWorlds()) {
            for (var entity : world.getEntities()) {
                if (!(entity instanceof Mob mobEntity)) continue;
                var pdc = mobEntity.getPersistentDataContainer();
                String uuidStr = pdc.get(key, PersistentDataType.STRING);
                if (uuidStr == null) continue;

                UUID uuid = UUID.fromString(uuidStr);
                if (activeMobs.containsKey(uuid)) continue; // bereits registriert

                // Hier musst du die passende Factory anhand der ID kennen
                String mobId = detectMobIdByEntity(mobEntity); // eigene Methode nötig
                if (mobId == null) continue;

                ICustomMob customMob = MOB_FACTORIES.get(mobId).get();
                customMob.setMob(mobEntity); // vorhandene Entity übernehmen
                activeMobs.put(uuid, customMob);
            }
        }
    }

    public void destroyAll() {
        for (ICustomMob mob : activeMobs.values()) mob.destroy();
        activeMobs.clear();
    }

    // Methode, um anhand der Entity z.B. über Name oder Typ den mobId-String zurückzugeben
    private String detectMobIdByEntity(Mob mobEntity) {
        String name = mobEntity.getCustomName();
        // z.B. Name-zu-ID Mapping oder eigene Logik
        for (var entry : MOB_FACTORIES.entrySet()) {
            // Beispiel: wir nehmen den Namen als ID-Marker
            if (entry.getKey().equalsIgnoreCase(name)) return entry.getKey();
        }
        return null;
    }
}
