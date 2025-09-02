package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class Hardcore extends CustomChallenge {

    public Hardcore(Plugin plugin) {
        super(plugin, "Hardcore");
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
