package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class TheCrazyMobs extends CustomChallenge {

    public TheCrazyMobs(Trolling trolling) {
        super(trolling, "TheCrazyMobs");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @ChallengeEvent
    public void onBatSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Bat)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("bat_drone", event.getLocation());
    }

    @ChallengeEvent
    public void onCreeperSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("gravity_creeper", event.getLocation());
    }

    @ChallengeEvent
    public void onSkeletonSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("machine_gun_skeleton", event.getLocation());
    }

    @ChallengeEvent
    public void onZombieSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (!zombie.isAdult()) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("zombie_mother", event.getLocation());
    }

    @ChallengeEvent
    public void onEndermanSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Enderman)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("swapping_man", event.getLocation());
    }


    @ChallengeEvent
    public void onSpiderSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Spider)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("deadly_spider", event.getLocation());
    }

    @ChallengeEvent
    public void onSheepSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Sheep)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("tnt_sheep", event.getLocation());
    }

    @ChallengeEvent
    public void onCowSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Cow)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("scared_cow", event.getLocation());
    }

    @ChallengeEvent
    public void onChickenSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Chicken)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("exploding_chicken", event.getLocation());
    }

    @ChallengeEvent
    public void onGolemSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Golem)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("protector_golem", event.getLocation());
    }

    @ChallengeEvent
    public void onPigSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Pig)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        event.setCancelled(true);
        trolling.getCustomMobData().spawn("stealing_pig", event.getLocation());
    }
}
