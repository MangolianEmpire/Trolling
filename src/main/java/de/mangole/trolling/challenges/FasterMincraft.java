package de.mangole.trolling.challenges;

import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class FasterMincraft extends CustomChallenge {

    public FasterMincraft(Plugin plugin) {
        super(plugin, "FasterMinecraft");
    }

    @Override
    protected void onActivate() {
        plugin.getServer().getServerTickManager().setTickRate(100);
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.setNoDamageTicks(2);
            player.setMaximumNoDamageTicks(2);
        });
    }

    @Override
    protected void onDeactivate() {
        plugin.getServer().getServerTickManager().setTickRate(20);
        plugin.getServer().getOnlinePlayers().forEach(player -> {
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
