package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MachineGunSkeleton extends CustomMob {

    public MachineGunSkeleton(Trolling trolling) {
        super(trolling, "machine_gun_skeleton", Skeleton.class);
    }

    @CustomMobEvent
    public void onShoot(EntityShootBowEvent event) {
        Skeleton skeleton = (Skeleton) this.mob;
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                skeleton.launchProjectile(Arrow.class);
                count++;
                if (count >= 4) {
                    cancel();
                }
            }
        }.runTaskTimer(trolling, 5L, 5L);
    }
}
