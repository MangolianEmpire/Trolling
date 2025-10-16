package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import org.bukkit.entity.Mob;

public abstract class CustomBoss extends CustomMob implements CustomLootDroppable {

    public CustomBoss(Trolling trolling, String id, Class<? extends Mob> mobClass, String name, boolean visibleName, double health) {
        super(trolling, id, mobClass, name, visibleName, health);
    }
}
