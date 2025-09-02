package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.CustomChallenge;
import de.mangole.trolling.utils.CustomChallengeItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class FallChallenge extends CustomChallenge {

    public FallChallenge(Plugin plugin) {
        super(plugin, "NoFallDamage");
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
