package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallChallenge extends CustomChallenge {

    public FallChallenge(Trolling trolling) {
        super(trolling, "NoFallDamage");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!isActive()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(player.getHealth());
        }
    }

}
