package de.mangole.trolling.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class CustomChallengeItem extends CustomInventoryItem {

    private final CustomChallenge challenge;

    public CustomChallengeItem(ItemStack item, CustomChallenge challenge) {
        super(item, player -> {
            if (challenge.isActive()) {
                challenge.deactivate();
            } else {
                challenge.activate();
            }
        });
        this.challenge = challenge;
    }

    public CustomChallenge getChallenge() {
        return challenge;
    }
}
