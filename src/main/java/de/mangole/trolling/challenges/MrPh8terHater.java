package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class MrPh8terHater extends CustomChallenge {

    private static final String VICTIM_NAME = "Sergey898";

    public MrPh8terHater(Trolling trolling) {
        super(trolling, "MrPh8terHater");
    }

    @Override
    protected void onActivate() {

    }

    @Override
    protected void onDeactivate() {

    }

    @EventHandler
    public void onEntitydamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getName().equals(VICTIM_NAME)) {
                double newDamage = event.getDamage() * 2.0;
                event.setDamage(newDamage);
            }
        }
    }
}
