package de.mangole.trolling.gamemodes;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.GameMode;
import de.mangole.trolling.Trolling;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GameModeChallenge extends GameModeBase {

    public GameModeChallenge(Trolling trolling) {
        super(trolling, GameMode.CHALLENGE);
    }

    @GameModeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        gameManager.loseGame();
    }
}
