package de.mangole.trolling.challenges;

import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.events.GameChangeEvent;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class NoOneLeftBehind extends CustomChallenge {

    private final String[] FUNNY_TEXTS = {
            "I'm so alone...",
            "I wish someone was here...",
            "Maybe I deserve this...",
            "Hello darkness my old friend...",
            "Why does nobody love me?",
            "Mommy...",
            "Is anyone here?",
            "Please, don't leave me alone!",
            "I don't want to die a virgin!",
            "Is anyone here?",
            "Please save me!"
    };

    public NoOneLeftBehind(Trolling trolling) {
        super(trolling, "No one left behind");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {

    }

    @ChallengeEvent
    public void onGameStarted(GameChangeEvent event) {
        if (event.getNewStatus() != GameStatus.RUNNING) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (trolling.getGameManager().getGameStatus() != GameStatus.RUNNING){
                    cancel();
                }
                for(Player player : trolling.getServer().getOnlinePlayers()){
                    List<Entity> nearbyEntities = player.getNearbyEntities(20, 20,20);
                    if (player.getGameMode() != GameMode.SURVIVAL) continue;
                    if (hasNearbyPlayers(nearbyEntities)) continue;
                    trolling.getServer().sendMessage(Component.text(player.getName() + ": " + FUNNY_TEXTS[new Random().nextInt(0, FUNNY_TEXTS.length - 1)]));
                    player.damage(2);
                }
            }
        }.runTaskTimer(trolling, 0L, 20L * 3);
    }

    private static boolean hasNearbyPlayers(List<Entity> nearbyEntities) {
        for (Entity entity : nearbyEntities){
            if (entity instanceof Player closePlayer && closePlayer.getGameMode() == GameMode.SURVIVAL){
                return true;
            }
        }
        return false;
    }
}
