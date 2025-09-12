package de.mangole.trolling.commands;

import de.mangole.trolling.GameManager;
import de.mangole.trolling.GameMode;
import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.events.GameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameChange implements CommandExecutor {

    private final Trolling trolling;
    private final GameManager gameManager;

    public GameChange(Trolling trolling) {
        this.trolling = trolling;
        this.gameManager = trolling.getGameManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            sender.sendMessage("wrong usage");
        }

        String status = args[0];

        if (status.equals("start")) {
            gameManager.startGame();
        }

        if (status.equals("pause")) {
            gameManager.pauseGame();
        }

        if (status.equals("resume")) {
            gameManager.resumeGame();
        }

        if (status.equals("lose")) {
            gameManager.loseGame();
        }

        if (status.equals("stop")) {
            gameManager.stopGame();
        }

        return true;
    }
}
