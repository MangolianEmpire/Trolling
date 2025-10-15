package de.mangole.trolling.customMobs;

import de.mangole.trolling.Trolling;
import org.bukkit.entity.Mob;

public abstract class CustomBoss extends CustomMob implements CustomLootDroppable {

    public CustomBoss(Trolling trolling, Mob mob) {
        super(trolling, mob);
    }

    public CustomBoss(Trolling trolling, Mob mob, String name) {
        super(trolling, mob, name);
    }

    public CustomBoss(Trolling trolling, Mob mob, String name, boolean visibleName) {
        super(trolling, mob, name, visibleName);
    }
}
