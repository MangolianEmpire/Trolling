package de.mangole.trolling.commands;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandExecutor {

    private final Trolling trolling;

    public WorldCommand(Trolling trolling) {
        this.trolling = trolling;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können den Befehl nutzen.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cVerwendung: /world <list|tp <weltname>>");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage("§aVerfügbare Welten:");
            for (World world : Bukkit.getWorlds()) {
                player.sendMessage("§7- " + world.getName());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (args.length < 2) {
                player.sendMessage("§cVerwendung: /world tp <weltname>");
                return true;
            }

            World targetWorld = Bukkit.getWorld(args[1]);
            if (targetWorld == null) {
                player.sendMessage("§cWelt nicht gefunden: " + args[1]);
                return true;
            }

            player.teleport(targetWorld.getSpawnLocation());
            player.sendMessage("§aTeleportiert nach §e" + targetWorld.getName());
            return true;
        }

        player.sendMessage("§cUnbekannter Subcommand: " + args[0]);
        return true;
    }
}
