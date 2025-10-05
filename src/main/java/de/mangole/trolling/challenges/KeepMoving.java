package de.mangole.trolling.challenges;

import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.events.GameChangeEvent;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KeepMoving extends CustomChallenge {

    private final HashMap<Block, Integer> changingBlocks = new HashMap<>();
    private BukkitTask keepMovingTask = null;

    public KeepMoving(Trolling trolling) {
        super(trolling, "Keep Moving");
    }

    @Override
    protected void onActivate() {
        if (keepMovingTask == null) {
            keepMovingTask = startKeepMovingTask();
        }
        changingBlocks.clear();
    }

    @Override
    protected void onDeactivate() {
        if (keepMovingTask != null) {
            keepMovingTask.cancel();
            keepMovingTask = null;
        }
        changingBlocks.clear();
    }

    @ChallengeEvent
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!event.hasChangedBlock()) return;
        if (!isOnGround(player)) return;
        if (trolling.getGameManager().getGameStatus() != GameStatus.RUNNING) return;

        Block block = event.getTo().clone().add(0, -1, 0).getBlock();
        if (changingBlocks.containsKey(block)) return;

        changingBlocks.put(block, 10);
    }

    @ChallengeEvent
    public void onGameEnd(GameChangeEvent event) {
        if (event.getNewStatus() == GameStatus.LOBBY) changingBlocks.clear();
    }

    private BukkitTask startKeepMovingTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : new ArrayList<>(changingBlocks.keySet())) {
                    if (changingBlocks.get(block) <= 0) {
                        changingBlocks.remove(block);
                        block.setType(Material.LAVA);
                        continue;
                    }

                    changingBlocks.put(block, changingBlocks.get(block) - 1);
                }
            }
        }.runTaskTimer(trolling, 0L, 20L);
    }

    private boolean isOnGround(Player player) {
        Location loc = player.getLocation();
        Location below = loc.clone().subtract(0, 0.1, 0);
        return below.getBlock().getType().isSolid();
    }
}
