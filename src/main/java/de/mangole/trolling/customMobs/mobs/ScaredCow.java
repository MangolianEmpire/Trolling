package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.entity.Cow;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ScaredCow extends CustomMob {

    public ScaredCow(Trolling trolling) {
        super(trolling, "scared_cow", Cow.class);
    }

    @CustomMobEvent
    public void onCowDeath(EntityDeathEvent event) {
        Cow cow = (Cow) this.mob;

        Random random = new Random();

        if (random.nextDouble() < 0.3) {
            event.setCancelled(true);
            cow.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 1));
            cow.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
        }
    }
}
