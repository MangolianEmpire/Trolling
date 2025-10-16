package de.mangole.trolling;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.mangole.trolling.challenges.ChallengeLoader;
import de.mangole.trolling.commands.ChallengeEdit;
import de.mangole.trolling.commands.GameChange;
import de.mangole.trolling.commands.WorldCommand;
import de.mangole.trolling.commands.WorldReset;
import de.mangole.trolling.customItems.CustomItemData;
import de.mangole.trolling.customMobs.CustomMobData;
import de.mangole.trolling.events.GameChangeListener;
import de.mangole.trolling.events.GameLogicListener;
import de.mangole.trolling.events.GamePortalListener;
import de.mangole.trolling.events.LobbyListener;
import de.mangole.trolling.utils.CustomInventoryListener;
import de.mangole.trolling.utils.DeathcounterManager;
import de.mangole.trolling.utils.HealthDisplay;
import de.mangole.trolling.utils.ReviveBeaconManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Trolling extends JavaPlugin implements Listener {

    private GameManager gameManager;
    private Data data;
    private WorldManager worldManager;
    private ChallengeLoader challengeLoader;
    private TimerManager timerManager;
    private ReviveBeaconManager reviveBeaconManager;
    private ProtocolManager protocolManager;
    private LobbyListener lobbyListener;
    private DeathcounterManager deathcounterManager;
    private CustomItemData customItemData;
    private CustomMobData customMobData;
    public static Plugin plugin;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        getLogger().info("Mein Plugin wurde geladen!");

        this.gameManager = new GameManager(this);
        this.deathcounterManager = new DeathcounterManager(this);
        this.customItemData = new CustomItemData(this);
        this.customMobData = new CustomMobData(this);

        registerEvents();
        registerCommands();
        plugin = this;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        this.challengeLoader = new ChallengeLoader(this);
        this.worldManager = new WorldManager(this);
        this.data = new Data(this);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.reviveBeaconManager = new ReviveBeaconManager(this);
        this.timerManager = new TimerManager(this);

        if (gameManager.getGameStatus() == GameStatus.LOST) {
            gameManager.stopGame();
        } else if (gameManager.getGameStatus() != GameStatus.LOBBY) {
            gameManager.pauseGame();
        }
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

        if (this.timerManager != null) {
            timerManager.saveTimer();
        }

        if (this.reviveBeaconManager != null) {
            reviveBeaconManager.saveBeacons();
        }

        if (this.deathcounterManager != null) {
            deathcounterManager.saveDeaths();
        }

        if (this.customMobData != null) {
            customMobData.destroyAll();
        }
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(lobbyListener = new LobbyListener(this), this);
        pluginManager.registerEvents(new CustomInventoryListener(this), this);
        pluginManager.registerEvents(new GamePortalListener(this), this);
        pluginManager.registerEvents(new GameChangeListener(this), this);
        pluginManager.registerEvents(new GameLogicListener(this), this);
        pluginManager.registerEvents(new HealthDisplay(this), this);
    }

    private void registerCommands() {
        getCommand("world").setExecutor(new WorldCommand(this));
        getCommand("worldreset").setExecutor(new WorldReset(this));
        getCommand("gamechange").setExecutor(new GameChange(this));
        getCommand("challengeedit").setExecutor(new ChallengeEdit(this));
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

    public TimerManager getTimerManager() {
        return this.timerManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public LobbyListener getLobbyListener() {
        return lobbyListener;
    }

    public CustomItemData getCustomItemData() {
        return customItemData;
    }

    public CustomMobData getCustomMobData() {
        return customMobData;
    }
}

