package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;


public class Heartbroken extends CustomChallenge {

    private final Trolling trolling;

    public Heartbroken(Trolling trolling) {
        super(trolling, "Heartbroken");
        this.trolling = trolling;
    }

    @Override
    protected void onActivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerMaxHealth(player, 1);
        }
    }

    @Override
    protected void onDeactivate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerMaxHealth(player, 20);
        }
    }

    public void setPlayerMaxHealth(Player player, double health) {
        Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(health);
        player.setHealth(health);
    }

    @ChallengeEvent
    public void onJoin(PlayerJoinEvent event) {
        setPlayerMaxHealth(event.getPlayer(), 1);
    }
}
