package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ZombieMother extends CustomMob {

    public ZombieMother(Trolling trolling) {
        super(trolling, "zombie_mother", Zombie.class, "Zombie Mother", false, 40);
    }

    @CustomMobEvent
    public void onZombieDeath(EntityDeathEvent event) {
        World world = getMob().getWorld();
        Location location = getMob().getLocation();

        Random random = new Random();
        int zombie_amount = random.nextInt(3);
        zombie_amount = zombie_amount + 2;
        for (int i = 0; i < zombie_amount; i++) {
            Zombie baby = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
            baby.setBaby();
            baby.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
            baby.setHealth(1);
        }
    }
}
