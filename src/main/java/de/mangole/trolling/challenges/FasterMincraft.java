package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FasterMincraft extends CustomChallenge {

    public FasterMincraft(Trolling trolling) {
        super(trolling, "FasterMinecraft");
    }

    @Override
    protected void onActivate() {
        trolling.getServer().getServerTickManager().setTickRate(100);
        trolling.getServer().getOnlinePlayers().forEach(player -> {
            player.setNoDamageTicks(2);
            player.setMaximumNoDamageTicks(2);
        });
    }

    @Override
    protected void onDeactivate() {
        trolling.getServer().getServerTickManager().setTickRate(20);
        trolling.getServer().getOnlinePlayers().forEach(player -> {
            player.setNoDamageTicks(10);
            player.setMaximumNoDamageTicks(10);
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setNoDamageTicks(2);
        p.setMaximumNoDamageTicks(2);
    }
}
