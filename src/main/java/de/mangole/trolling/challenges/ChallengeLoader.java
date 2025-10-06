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

        //Create all Challeges
        FallChallenge fallChallenge = new FallChallenge(trolling);
        challenges.add(fallChallenge);

        TheCrazyMobs theCrazyMobs = new TheCrazyMobs(trolling);
        challenges.add(theCrazyMobs);

        FasterMincraft fasterMincraft = new FasterMincraft(trolling);
        challenges.add(fasterMincraft);

        WeirdChests weirdChests = new WeirdChests(trolling);
        challenges.add(weirdChests);

        Communism communism = new Communism(trolling);
        challenges.add(communism);

        Hardcore hardcore = new Hardcore(trolling);
        challenges.add(hardcore);

        Shortsightedness shortsightedness = new Shortsightedness(trolling);
        challenges.add(shortsightedness);

        FloodedWorld floodedWorld = new FloodedWorld(trolling);
        challenges.add(floodedWorld);

        MiniBosses miniBosses = new MiniBosses(trolling);
        challenges.add(miniBosses);

        ChorusInfection chorusInfection = new ChorusInfection(trolling);
        challenges.add(chorusInfection);

        Heartbroken heartbroken = new Heartbroken(trolling);
        challenges.add(heartbroken);

        MrPh8terHater mrPh8terHater = new MrPh8terHater(trolling);
        mrPh8terHater.activate();
        challenges.add(mrPh8terHater);

        LandMines landMines = new LandMines(trolling);
        challenges.add(landMines);

        NoOneLeftBehind noOneLeftBehind = new NoOneLeftBehind(trolling);
        challenges.add(noOneLeftBehind);

        KeepMoving keepMoving = new KeepMoving(trolling);
        challenges.add(keepMoving);

        DontTouch dontTouch = new DontTouch(trolling);
        challenges.add(dontTouch);

        // active saved challenges
        for (CustomChallenge challenge : challenges) {
            boolean active = config.getBoolean("challenges." + challenge.getChallengeName(), false);
            if (active) challenge.activate();
        }

        // FallDamage
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.FEATHER,
                Component.text("No Falldamage", NamedTextColor.WHITE),
                Component.text("Die instantly on any fall damage", NamedTextColor.WHITE),
                fallChallenge
        ));

        // TheCrazyMobs
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.SKELETON_SKULL,
                Component.text("TheCrazyMobs", NamedTextColor.DARK_GREEN),
                Component.text("Fear the dusk", NamedTextColor.DARK_GREEN),
                theCrazyMobs
        ));

        // Faster Minecraft
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.CLOCK,
                Component.text("Minecraft on Speed", NamedTextColor.AQUA),
                Component.text("Time flies if you have fun :)", NamedTextColor.AQUA),
                fasterMincraft
        ));

        // WeirdChests
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.CHEST,
                Component.text("WeirdChests", NamedTextColor.DARK_PURPLE),
                Component.text("Every opening is a gamble hehe", NamedTextColor.DARK_PURPLE),
                weirdChests
        ));

        // Communism
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.GOLDEN_PICKAXE,
                Component.text("Communism", NamedTextColor.DARK_RED),
                Component.text("Share your health and food", NamedTextColor.DARK_RED),
                communism
        ));

        // Hardcore
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.GOLDEN_APPLE,
                Component.text("Hardcore", NamedTextColor.LIGHT_PURPLE),
                Component.text("No natural regeneration", NamedTextColor.LIGHT_PURPLE),
                hardcore
        ));

        // Shortsightedness
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.SPYGLASS,
                Component.text("Chris' normal vision", NamedTextColor.BLACK),
                Component.text("Where tf are my glasses", NamedTextColor.BLACK),
                shortsightedness
        ));

        // FloodedWorld
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.TROPICAL_FISH_BUCKET,
                Component.text("Atlantis", NamedTextColor.BLUE),
                Component.text("Climate Change kicks your balls", NamedTextColor.BLUE),
                floodedWorld
        ));

        // Minibosses
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.MACE,
                Component.text("Minibosses", NamedTextColor.GOLD),
                Component.text("They are dangerous and bring custom loot with them!", NamedTextColor.GOLD),
                miniBosses
        ));

        // ChorusInfection
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.CHORUS_FRUIT,
                Component.text("Chorus Infection", NamedTextColor.DARK_PURPLE),
                Component.text("Why does the food taste like that?", NamedTextColor.DARK_PURPLE),
                chorusInfection
        ));

        // Heartbroken
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.GHAST_TEAR,
                Component.text("Heartbroken", NamedTextColor.RED),
                Component.text("Why did she leave me :(", NamedTextColor.RED),
                heartbroken
        ));

        // LandMines
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.TNT,
                Component.text("LandMines", NamedTextColor.DARK_RED),
                Component.text("Watch your steps", NamedTextColor.DARK_RED),
                landMines
        ));

        //NoOneLeftBehind
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.HEART_POTTERY_SHERD,
                Component.text("No One Left Behind", NamedTextColor.LIGHT_PURPLE),
                Component.text("Real friends are always by your side", NamedTextColor.LIGHT_PURPLE),
                noOneLeftBehind
        ));

        //KeepMoving
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.LAVA_BUCKET,
                Component.text("Keep Moving", NamedTextColor.GOLD),
                Component.text("The floor gets hot if you stay still for too long", NamedTextColor.GOLD),
                keepMoving
        ));

        //DontTouch
        customChallengeItems.add(CustomChallengeItemUtils.createCustomChallengeItem(
                Material.LEATHER_BOOTS,
                Component.text("Dont touch that block", NamedTextColor.DARK_RED),
                Component.text("The floor is lava but not every floor", NamedTextColor.DARK_RED),
                dontTouch
        ));
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
