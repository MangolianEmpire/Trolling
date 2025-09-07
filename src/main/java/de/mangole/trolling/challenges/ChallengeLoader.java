package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import de.mangole.trolling.utils.CustomChallengeItem;
import de.mangole.trolling.utils.CustomChallengeItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChallengeLoader {

    private final Trolling trolling;
    private final File file;
    private final YamlConfiguration config;

    public static List<CustomChallengeItem> customChallengeItems = new ArrayList<>();
    public static List<CustomChallenge> challenges = new ArrayList<>();

    public ChallengeLoader(Trolling trolling) {
        this.trolling = trolling;
        this.file = new File(trolling.getDataFolder(), "challenges.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        loadChallenges();
    }


    private void loadChallenges() {
        // FallDamage
        FallChallenge fallChallenge = new FallChallenge(trolling);
        challenges.add(fallChallenge);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.FEATHER,
                Component.text("No Falldamage", NamedTextColor.WHITE),
                Component.text("Die instantly on any fall damage", NamedTextColor.WHITE),
                fallChallenge
        ));

        // TheCrazyMobs
        TheCrazyMobs theCrazyMobs = new TheCrazyMobs(trolling);
        challenges.add(theCrazyMobs);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.SKELETON_SKULL,
                Component.text("TheCrazyMobs", NamedTextColor.DARK_GREEN),
                Component.text("Fear the dusk", NamedTextColor.DARK_GREEN),
                theCrazyMobs
        ));

        // Faster Minecraft
        FasterMincraft fasterMincraft = new FasterMincraft(trolling);
        challenges.add(fasterMincraft);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.CLOCK,
                Component.text("Minecraft on Speed", NamedTextColor.AQUA),
                Component.text("Time flies if you have fun :)", NamedTextColor.AQUA),
                fasterMincraft
        ));

        // WeirdChests
        WeirdChests weirdChests = new WeirdChests(trolling);
        challenges.add(weirdChests);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.CHEST,
                Component.text("WeirdChests", NamedTextColor.DARK_PURPLE),
                Component.text("Every opening is a gamble hehe", NamedTextColor.DARK_PURPLE),
                weirdChests
        ));

        // Communism
        Communism communism = new Communism(trolling);
        challenges.add(communism);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.GOLDEN_PICKAXE,
                Component.text("Communism", NamedTextColor.DARK_RED),
                Component.text("Share your health and food", NamedTextColor.DARK_RED),
                communism
        ));

        // Hardcore
        Hardcore hardcore = new Hardcore(trolling);
        challenges.add(hardcore);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.GOLDEN_APPLE,
                Component.text("Hardcore", NamedTextColor.LIGHT_PURPLE),
                Component.text("No natural regeneration", NamedTextColor.LIGHT_PURPLE),
                hardcore
        ));

        Shortsightedness shortsightedness = new Shortsightedness(trolling);
        challenges.add(shortsightedness);

        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.SPYGLASS,
                Component.text("Chris' normal vision", NamedTextColor.BLACK),
                Component.text("Where tf are my glasses", NamedTextColor.BLACK),
                shortsightedness
        ));

        // FloodedWorld
        FloodedWorld floodedWorld = new FloodedWorld(plugin);
        challenges.add(floodedWorld);

        ItemStack floodedWorldItemStack = new ItemStack(Material.TROPICAL_FISH_BUCKET);
        ItemMeta floodedWorldItemMeta = floodedWorldItemStack.getItemMeta();
        floodedWorldItemMeta.displayName(Component.text("Atlantis", NamedTextColor.BLUE));
        floodedWorldItemMeta.lore(List.of(Component.text("Klimaerwärmung kickt hart", NamedTextColor.BLUE)));
        floodedWorldItemStack.setItemMeta(floodedWorldItemMeta);
        CustomChallengeItem floodedWorldItem = new CustomChallengeItem(floodedWorldItemStack, floodedWorld);
        customChallengeItems.add(floodedWorldItem);


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
