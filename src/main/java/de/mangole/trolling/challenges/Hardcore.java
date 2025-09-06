package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public class Hardcore extends CustomChallenge {

    public Hardcore(Trolling trolling) {
        super(trolling, "Hardcore");
    }

    @Override
    protected void onActivate() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        }
    }

    @Override
    protected void onDeactivate() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.NATURAL_REGENERATION, true);
        }
    }
}
