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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ReviveBeacon implements Listener {

    private final Trolling trolling;
    private Player revivePlayer;
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
                foundationMats[i] = foundationBlock.getType();
                foundationBlock.setType(Material.IRON_BLOCK);
                i++;
            }
        }

        reviveTask = startReviveTask();
    }

    private BukkitTask startReviveTask() {
        Location loc = block.getLocation().clone().add(0.0, 1, 0.0);

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

                int playersInRange = getPlayersInRange();
                if (playersInRange > 0 && counter % 10 == 0) {
                    loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
                }
                if (revivePlayer.isOnline())
                    reviveProgress += playersInRange;
                counter++;
            }
        }.runTaskTimer(trolling, 0, 1L);
    }

    private int getPlayersInRange() {
        int count = 0;
        World world = block.getWorld();
        Location loc = block.getLocation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(revivePlayer) && player.getGameMode() == GameMode.SURVIVAL && player.getWorld().equals(world)) {
                if (player.getLocation().distance(loc) < reviveRange) count++;
            }
        }
        return count;
    }

    private void updateReviveProgressText() {
        double percent = (double) reviveProgress / reviveDuration;
        if (progressDisplay != null) {
            progressDisplay.text(Component.text(String.format("%.0f%%", percent * 100), NamedTextColor.YELLOW));
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
        revivePlayer.teleport(block.getLocation().add(0.5, 0.0, 0.5));
        revivePlayer.clearActivePotionEffects();
        revivePlayer.getInventory().clear();
        revivePlayer.setGameMode(GameMode.SURVIVAL);
        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(revivePlayer, block.getLocation(), false, false, false, PlayerRespawnEvent.RespawnReason.PLUGIN));
    }

    public void cleanup() {
        block.setType(oldBlock);
        for (int i = 0; i < foundation.length; i++) {
            foundation[i].setType(foundationMats[i]);
        }

        if (progressDisplay != null) progressDisplay.remove();
        HandlerList.unregisterAll(this);

        if (reviveTask != null && !reviveTask.isCancelled()) reviveTask.cancel();

        GameModeFortnite.reviveBeacons.remove(this);
        revivePlayer();
    }

    public void setRevivePlayer(Player revivePlayer) {
        this.revivePlayer = revivePlayer;
    }

    public Player getRevivePlayer() { return revivePlayer; }

    public Location getReviveLocation() { return block.getLocation(); }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().remove(block);
        for (Block b : foundation) event.blockList().remove(b);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().remove(block);
        for (Block b : foundation) event.blockList().remove(b);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.equals(this.block)) event.setCancelled(true);
        for (Block b : foundation)
            if (b.equals(block)) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (event.getBlock().equals(block)) event.setCancelled(true);
        for (Block b : foundation) if (event.getBlock().equals(b)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().equals(block)) event.setCancelled(true);
            for (Block b : foundation) if (event.getClickedBlock().equals(b)) event.setCancelled(true);
        }
    }

}
