package de.mangole.trolling.utils;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.gamemodes.GameModeFortnite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ReviveBeacon implements Listener {

    private final Trolling trolling;
    private final Player revivePlayer;
    private final double reviveRange;
    private int reviveProgress = 0;
    private final int reviveDuration;
    private final Material oldBlock;
    private final Block block;
    private final Block[] foundation;
    private final Material[] foundationMats;
    private final BukkitTask reviveTask;
    private TextDisplay progressDisplay;

    public ReviveBeacon(Trolling trolling, Player revivePlayer, int reviveRange, int reviveDuration, Block block) {
        this.trolling = trolling;
        this.revivePlayer = revivePlayer;
        this.reviveRange = reviveRange;
        this.reviveDuration = reviveDuration;
        this.block = block;

        Bukkit.getPluginManager().registerEvents(this, trolling);

        oldBlock = block.getType();
        block.setType(Material.BEACON);
        foundation = new Block[9];
        foundationMats = new Material[9];
        int i = 0;
        for (int x = block.getX() - 1; x <= block.getX() + 1; x++) {
            for (int z = block.getZ() - 1; z <= block.getZ() + 1; z++) {
                Block foundationBlock = block.getWorld().getBlockAt(x, block.getY() - 1, z);
                foundation[i] = foundationBlock;
                Material oldMat = foundationBlock.getType();
                foundationMats[i] = oldMat;
                foundationBlock.setType(Material.IRON_BLOCK);
                i++;
            }
        }

        reviveTask = reviveTask();
    }


    private BukkitTask reviveTask() {
        long period = 1;
        for (CustomChallenge challenge : trolling.getChallengeLoader().getCustomChallenges()) {
            if (challenge.getChallengeName().equals("FasterMinecraft") && challenge.isActive()) {
                period = 5;
                break;
            }
        }

        Location loc = block.getLocation().clone().add(0.5, 1, 0.5);
        if (progressDisplay == null || progressDisplay.isDead()) {
            progressDisplay = block.getWorld().spawn(loc, TextDisplay.class, textDisplay -> {
                textDisplay.setBillboard(Display.Billboard.VERTICAL);
                textDisplay.setBackgroundColor(Color.GREEN);
            });
        }

        return new BukkitRunnable() {
            long counter = 0;

            @Override
            public void run() {

                updateReviveProgressText();

                if (counter % 5 == 0) showReviveParticle();

                if (reviveProgress >= reviveDuration) {
                    cleanup();
                    cancel();
                    return;
                }

                int playerInRange = getPlayersInRange();
                if (playerInRange > 0 && counter % 10 == 0) {
                    loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
                }
                reviveProgress += playerInRange;
                counter++;
            }
        }.runTaskTimer(trolling, 0, period);
    }

    private int getPlayersInRange() {
        int numberPlayers = 0;
        World world = block.getWorld();
        Location location = block.getLocation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(world) && !player.equals(revivePlayer) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (player.getLocation().distance(location) < reviveRange) {
                    numberPlayers++;
                }
            }
        }
        return numberPlayers;
    }

    private void updateReviveProgressText() {
        double percentProgress = (double) reviveProgress / reviveDuration;
        if (progressDisplay != null) {
            progressDisplay.text(Component.text(
                    String.format("%.0f%%", percentProgress * 100), NamedTextColor.YELLOW
            ));
        }
    }

    private void showReviveParticle() {
        Location center = block.getLocation().clone().add(0.5, 0.5, 0.5);
        for (int i = 0; i < 100; i++) {
            double xOffset = Math.sin((double) i / 99 * Math.PI * 2) * reviveRange;
            double zOffset = Math.cos((double) i / 99 * Math.PI * 2) * reviveRange;

            Particle.PORTAL.builder()
                    .location(center)
                    .offset(xOffset, 0, zOffset)
                    .count(1)
                    .receivers(32, true)
                    .spawn();
        }
    }

    private void revivePlayer() {
        revivePlayer.teleport(block.getLocation());
        revivePlayer.clearActivePotionEffects();
        revivePlayer.getInventory().clear();
        revivePlayer.setGameMode(GameMode.SURVIVAL);
        World world = block.getWorld();
        world.playSound(block.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }

    public void cleanup() {
        revivePlayer();
        block.setType(oldBlock);
        for (int i = 0; i < foundationMats.length; i++) {
            foundation[i].setType(foundationMats[i]);
        }
        progressDisplay.remove();

        HandlerList.unregisterAll(this);

        if (reviveTask != null && !reviveTask.isCancelled()) {
            reviveTask.cancel();
        }


        GameModeFortnite.reviveBeacons.remove(this);
    }

    public Player getRevivePlayer() {
        return revivePlayer;
    }

    public Location getReviveLocation() {
        return block.getLocation();
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().equals(block)) event.setCancelled(true);

        for (Block value : foundation) {
            if (event.getBlock().equals(value)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (event.getBlock().equals(block)) event.setCancelled(true);

        for (Block value : foundation) {
            if (event.getBlock().equals(value)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        if (event.getBlock().equals(block)) event.setCancelled(true);

        for (Block value : foundation) {
            if (event.getBlock().equals(value)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().equals(block)) event.setCancelled(true);

        for (Block value : foundation) {
            if (event.getClickedBlock().equals(value)) {
                event.setCancelled(true);
            }
        }
    }
}
