package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class StealingPig extends CustomMob {

    public StealingPig(Trolling trolling) {
        super(trolling, "stealing_pig", Pig.class);
    }

    @CustomMobEvent
    public void onPigDeath(EntityDeathEvent event) {
        Pig pig = (Pig) this.mob;
        if (!(pig.getKiller() instanceof Player killer)) return;

        if (pig.isAdult()) {
            for (ItemStack item : killer.getInventory().getContents()) {
                if (item != null) {
                    pig.getWorld().dropItemNaturally(pig.getLocation(), item);
                }
            }
            killer.getInventory().clear();
        } else {
            killer.getWorld().playSound(killer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            killer.getInventory().clear();
        }
    }
}
