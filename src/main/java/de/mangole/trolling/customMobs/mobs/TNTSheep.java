package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class TNTSheep extends CustomMob {

    public TNTSheep(Trolling trolling) {
        super(trolling, "tnt_sheep", Sheep.class);
    }

    @CustomMobEvent
    public void onSheepDeath(EntityDeathEvent event) {
        Sheep sheep = (Sheep) this.mob;
        Random random = new Random();

        if (random.nextDouble() < 0.2) {
            Location loc = sheep.getLocation().clone();
            World world = sheep.getWorld();

            // TNT-Richtungen (N, O, S, W)
            Vector[] directions = {
                    new Vector(1, 0.3, 0),   // Osten
                    new Vector(-1, 0.3, 0),  // Westen
                    new Vector(0, 0.3, 1),   // Süden
                    new Vector(0, 0.3, -1)   // Norden
            };

            for (Vector dir : directions) {
                TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc, EntityType.TNT);
                tnt.setFuseTicks(40); // 3 Sekunden bis Explosion (20 Ticks = 1 Sekunde)
                tnt.setVelocity(dir.multiply(0.5)); // Stärke anpassen, z.B. 0.5 für "nicht so weit"
            }
        }
    }
}
