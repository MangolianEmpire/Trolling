package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.entity.Player;
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
        trolling.getTimerManager().getTimerCounter().setPeriod(100);
        trolling.getLobbyListener().setPeriod(100);
    }

    @Override
    protected void onDeactivate() {
        trolling.getServer().getServerTickManager().setTickRate(20);
        trolling.getServer().getOnlinePlayers().forEach(player -> {
            player.setNoDamageTicks(10);
            player.setMaximumNoDamageTicks(10);
        });
        trolling.getTimerManager().getTimerCounter().setPeriod(20);
        trolling.getLobbyListener().setPeriod(20);
    }

    @ChallengeEvent
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setNoDamageTicks(2);
        p.setMaximumNoDamageTicks(2);
    }
}
