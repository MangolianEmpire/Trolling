package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Golem;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ProtectorGolem extends CustomMob {

    public ProtectorGolem(Trolling trolling) {
        super(trolling, "protector_golem", IronGolem.class);
    }

    @CustomMobEvent
    public void onGolemMove(EntityMoveEvent event) {
        Golem golem = (Golem) this.mob;
        golem.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        Player target = getNearestPlayer(golem);
        if (target != null) {
            golem.setTarget(target);
        }
    }

    private Player getNearestPlayer(LivingEntity golem) {
        double nearestDistance = Double.MAX_VALUE;
        Player nearestPlayer = null;

        for (Player player : golem.getWorld().getPlayers()) {
            if (!player.isDead() && (player.getGameMode() == GameMode.SURVIVAL)) {
                double distance = player.getLocation().distanceSquared(golem.getLocation());
                if (distance < 400 && distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPlayer = player;
                }
            }
        }

        return nearestPlayer;
    }
}
