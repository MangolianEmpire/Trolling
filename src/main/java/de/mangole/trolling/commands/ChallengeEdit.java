package de.mangole.trolling.commands;

import de.mangole.trolling.*;
import de.mangole.trolling.events.GameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChallengeEdit implements CommandExecutor {

    private final Trolling trolling;

    public ChallengeEdit(Trolling trolling) {
        this.trolling = trolling;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return false;

        Data.lobbySettingsInventory.open(player);

        return true;
    }
}
