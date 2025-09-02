package de.mangole.trolling;

import de.mangole.trolling.challenges.ChallengeLoader;
import de.mangole.trolling.commands.GameChange;
import de.mangole.trolling.commands.WorldCommand;
import de.mangole.trolling.commands.WorldReset;
import de.mangole.trolling.events.*;
import de.mangole.trolling.utils.CustomInventoryListener;
import de.mangole.trolling.utils.TimeCounter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Trolling extends JavaPlugin {

    private GameManager gameManager;
    private Data data;
    private WorldManager worldManager;
    private ChallengeLoader challengeLoader;
    private TimeCounter timer;
    public static Plugin plugin;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        getLogger().info("Mein Plugin wurde geladen!");

        this.worldManager = new WorldManager(this);
        this.gameManager = new GameManager(this);
        this.challengeLoader = new ChallengeLoader(this);
        this.data = new Data(this);
        this.timer = new TimeCounter(this);

        registerEvents();
        registerCommands();
        plugin = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("Mein Plugin wurde gestoppt.");
        if (this.gameManager != null) {
            gameManager.saveStatus();
        }
        if (this.challengeLoader != null) {
            challengeLoader.saveChallenges();
        }
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new LobbyListener(this), this);
        pluginManager.registerEvents(new CustomInventoryListener(this), this);
        pluginManager.registerEvents(new GamePortalListener(this), this);
        pluginManager.registerEvents(new GameChangeListener(this), this);
        pluginManager.registerEvents(new GameLogicListener(this), this);
        pluginManager.registerEvents(new GameFortniteListener(this), this);
    }

    private void registerCommands() {
        getCommand("world").setExecutor(new WorldCommand());
        getCommand("worldreset").setExecutor(new WorldReset());
        getCommand("gamechange").setExecutor(new GameChange(this));
    }

    public Data getData() {
        return data;
    }

    public ChallengeLoader getChallengeLoader() {
        return challengeLoader;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public TimeCounter getTimer() {
        return this.timer;
    }
}

