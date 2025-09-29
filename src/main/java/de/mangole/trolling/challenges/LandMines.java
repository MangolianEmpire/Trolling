package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;

public class LandMines extends CustomChallenge {

    private final Random random = new Random();

    public LandMines(Trolling trolling) {
        super(trolling, "LandMines");
    }

    @Override
    protected void onActivate() {
        trolling.getServer().getPluginManager().registerEvents(this, trolling);
    }

    @Override
    protected void onDeactivate() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (random.nextDouble() < 0.1) { // 10% Chance.
            World world = event.getBlock().getWorld();
            Location loc = event.getBlock().getLocation();
            world.createExplosion(loc, 30.0f, true, true); // 5x Charged Creeper, Blockschaden an.
        }
    }
}