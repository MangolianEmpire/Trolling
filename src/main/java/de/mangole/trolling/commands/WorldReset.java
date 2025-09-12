package de.mangole.trolling.commands;

import de.mangole.trolling.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Random;

public class WorldReset implements CommandExecutor {

    private final Trolling trolling;
    private final GameManager gameManager;

    public WorldReset(Trolling trolling) {
        this.trolling = trolling;
        this.gameManager = trolling.getGameManager();
    }

    private final String[] customWorlds = {"game_overworld", "game_nether", "game_end"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Server server = Bukkit.getServer();

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /worldreset <light|hard>");
            return false;
        }

        if (gameManager.getGameStatus() != GameStatus.LOBBY) {
            sender.sendMessage("Not possible while the Game is running");
            return false;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getLocation().getWorld().equals(WorldManager.lobbyWorld))
                p.teleport(WorldManager.lobbySpawn);
        }

        long newSeed;

        if (args[0].equalsIgnoreCase("light")) {
            newSeed = Bukkit.getWorld("game_overworld").getSeed();
            Component text = Component.text("Worlds will get resetted with same Seed: ", NamedTextColor.GREEN)
                    .append(Component.text(newSeed, NamedTextColor.WHITE));
            server.sendMessage(text);
        } else if (args[0].equalsIgnoreCase("hard")) {
            newSeed = new Random().nextLong();
            Component text = Component.text("Worlds will get resetted with new Seed: ", NamedTextColor.GREEN)
                    .append(Component.text(newSeed, NamedTextColor.WHITE));
            server.sendMessage(text);
        } else {
            sender.sendMessage("§cUsage: /worldreset <light|hard>");
            return false;
        }

        for (String worldName : customWorlds) {
            resetWorld(worldName, newSeed, sender);
        }

        return true;
    }

    private void resetWorld(String worldName, long seed, CommandSender sender) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            sender.sendMessage("§eWelt entladen: " + worldName);
            deleteDirectory(world.getWorldFolder());
            sender.sendMessage("§cWelt gelöscht: " + worldName);
        }

        // neue Welt mit Seed generieren
        Bukkit.getScheduler().runTaskLater(trolling, () -> {
            WorldCreator wc = new WorldCreator(worldName).seed(seed);

            // Nether / End explizit setzen
            if (worldName.endsWith("nether")) {
                wc.environment(World.Environment.NETHER);
            } else if (worldName.endsWith("end")) {
                wc.environment(World.Environment.THE_END);
            } else {
                wc.environment(World.Environment.NORMAL);
            }

            World newWorld = Bukkit.createWorld(wc);
            if (newWorld != null) {
                sender.sendMessage("§aNeu erstellt: " + newWorld.getName() + " (Seed: " + seed + ")");
            } else {
                sender.sendMessage("§cFehler beim Erstellen von " + worldName);
            }
        }, 20L);
    }

    private void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            path.delete();
        }
    }
}
