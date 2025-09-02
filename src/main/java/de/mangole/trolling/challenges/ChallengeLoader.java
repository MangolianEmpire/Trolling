package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.CustomChallenge;
import de.mangole.trolling.utils.CustomChallengeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChallengeLoader {

    private final Plugin plugin;
    private final File file;
    private final YamlConfiguration config;

    public static List<CustomChallengeItem> customChallengeItems = new ArrayList<>();
    public static List<CustomChallenge> challenges = new ArrayList<>();

    public ChallengeLoader(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "challenges.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        loadChallenges();
    }


    private void loadChallenges() {
        // FallDamage
        FallChallenge fallChallenge = new FallChallenge(plugin);
        challenges.add(fallChallenge);

        ItemStack fallDamage = new ItemStack(Material.FEATHER);
        ItemMeta fallDamage_meta = fallDamage.getItemMeta();
        fallDamage_meta.displayName(Component.text("No Falldamage", NamedTextColor.WHITE));
        fallDamage_meta.lore(List.of(Component.text("Die instantly on any fall damage", NamedTextColor.WHITE)));
        fallDamage.setItemMeta(fallDamage_meta);
        CustomChallengeItem fallDamageItem = new CustomChallengeItem(fallDamage, fallChallenge);
        customChallengeItems.add(fallDamageItem);

        // TheCrazyMobs
        TheCrazyMobs theCrazyMobs = new TheCrazyMobs(plugin);
        challenges.add(theCrazyMobs);

        ItemStack crazyMobsItemStack = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta crazyMobsItemMeta = crazyMobsItemStack.getItemMeta();
        crazyMobsItemMeta.displayName(Component.text("TheCrazyMobs", NamedTextColor.DARK_GREEN));
        crazyMobsItemMeta.lore(List.of(Component.text("Fear the dusk", NamedTextColor.DARK_GREEN)));
        crazyMobsItemStack.setItemMeta(crazyMobsItemMeta);
        CustomChallengeItem crazyMobsItem = new CustomChallengeItem(crazyMobsItemStack, theCrazyMobs);
        customChallengeItems.add(crazyMobsItem);

        // Faster Minecraft
        FasterMincraft fasterMincraft = new FasterMincraft(plugin);
        challenges.add(fasterMincraft);

        ItemStack fasterMinecraftItemStack = new ItemStack(Material.CLOCK);
        ItemMeta fasterMinecraftItemMeta = fasterMinecraftItemStack.getItemMeta();
        fasterMinecraftItemMeta.displayName(Component.text("Minecraft on Speed", NamedTextColor.AQUA));
        fasterMinecraftItemMeta.lore(List.of(Component.text("The Flash sees everything in SlowMotion. Unfortunately, you are not the Flash :(", NamedTextColor.AQUA)));
        fasterMinecraftItemStack.setItemMeta(fasterMinecraftItemMeta);
        CustomChallengeItem fasterMinecraftItem = new CustomChallengeItem(fasterMinecraftItemStack, fasterMincraft);
        customChallengeItems.add(fasterMinecraftItem);

        // Faster Minecraft
        WeirdChests weirdChests = new WeirdChests(plugin);
        challenges.add(weirdChests);

        ItemStack weirdChestsItemStack = new ItemStack(Material.CHEST);
        ItemMeta weirdChestsItemMeta = weirdChestsItemStack.getItemMeta();
        weirdChestsItemMeta.displayName(Component.text("WeirdChests", NamedTextColor.DARK_PURPLE));
        weirdChestsItemMeta.lore(List.of(Component.text("Every opening is a gamble hehe", NamedTextColor.DARK_PURPLE)));
        weirdChestsItemStack.setItemMeta(weirdChestsItemMeta);
        CustomChallengeItem weirdChestsItem = new CustomChallengeItem(weirdChestsItemStack, weirdChests);
        customChallengeItems.add(weirdChestsItem);

        // Communism
        Communism communism = new Communism(plugin);
        challenges.add(communism);

        ItemStack communismItemStack = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta communismItemMeta = communismItemStack.getItemMeta();
        communismItemMeta.displayName(Component.text("Communism", NamedTextColor.DARK_RED));
        communismItemMeta.lore(List.of(Component.text("Share your health and food", NamedTextColor.DARK_RED)));
        communismItemStack.setItemMeta(communismItemMeta);
        CustomChallengeItem communismItem = new CustomChallengeItem(communismItemStack, communism);
        customChallengeItems.add(communismItem);

        // Hardcore
        Hardcore hardcore = new Hardcore(plugin);
        challenges.add(hardcore);

        ItemStack hardcoreItemStack = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta hardcoreItemMeta = hardcoreItemStack.getItemMeta();
        hardcoreItemMeta.displayName(Component.text("Hardcore", NamedTextColor.LIGHT_PURPLE));
        hardcoreItemMeta.lore(List.of(Component.text("No natural regeneration", NamedTextColor.LIGHT_PURPLE)));
        hardcoreItemStack.setItemMeta(hardcoreItemMeta);
        CustomChallengeItem hardcoreItem = new CustomChallengeItem(hardcoreItemStack, hardcore);
        customChallengeItems.add(hardcoreItem);

        // active saved challenges
        for (CustomChallenge challenge : challenges) {
            boolean active = config.getBoolean("challenges." + challenge.getChallengeName(), false);
            if (active) challenge.activate();
        }
    }

    public void saveChallenges() {
        try {
            for (CustomChallenge challenge : challenges) {
                config.set("challenges." + challenge.getChallengeName(), challenge.isActive());
            }
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<CustomChallenge> getCustomChallenges() {
        return challenges;
    }
}
