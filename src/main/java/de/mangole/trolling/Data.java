package de.mangole.trolling;

import de.mangole.trolling.challenges.ChallengeLoader;
import de.mangole.trolling.utils.CustomChallengeItem;
import de.mangole.trolling.utils.CustomInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Data {

    private final Plugin plugin;

    public static ItemStack lobbySpawnTeleporter;
    public static ItemStack lobbyGameModeChanger;
    public static ItemStack lobbySettings;
    public static CustomInventory lobbySettingsInventory;

    public Data(Plugin plugin) {
        this.plugin = plugin;


        loadItems();
        loadCustomInventories();
    }

    private void loadItems() {
        ItemStack lobbySpawn = new ItemStack(Material.EMERALD);
        ItemMeta spawn_meta = lobbySpawn.getItemMeta();
        spawn_meta.displayName(Component.text("Spawn", NamedTextColor.AQUA));
        spawn_meta.lore(List.of(Component.text("Teleports you back to the Lobby Spawn", NamedTextColor.AQUA)));
        lobbySpawn.setItemMeta(spawn_meta);
        lobbySpawnTeleporter = lobbySpawn;

        ItemStack settings = new ItemStack(Material.REPEATER);
        ItemMeta settings_meta = settings.getItemMeta();
        settings_meta.displayName(Component.text("Settings", NamedTextColor.DARK_RED));
        settings_meta.lore(List.of(Component.text("Change settings for your next Minecraft run", NamedTextColor.DARK_RED)));
        settings.setItemMeta(settings_meta);
        lobbySettings = settings;

        ItemStack gameModeChanger = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta gameMode_meta = gameModeChanger.getItemMeta();
        gameMode_meta.displayName(Component.text("GameMode", NamedTextColor.RED));
        gameMode_meta.lore(List.of(Component.text("Change GameMode for your next Minecraft run", NamedTextColor.RED)));
        gameModeChanger.setItemMeta(gameMode_meta);
        lobbyGameModeChanger = gameModeChanger;
    }

    public void loadCustomInventories() {
        lobbySettingsInventory = new CustomInventory(plugin, 4, Component.text("Settings"));
        int i = 0;
        for (CustomChallengeItem item : ChallengeLoader.customChallengeItems) {
            lobbySettingsInventory.setItem(i, item);
            i++;
        }
    }
}
