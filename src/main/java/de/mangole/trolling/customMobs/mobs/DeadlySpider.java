package de.mangole.trolling.customMobs.mobs;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.customMobs.CustomMob;
import de.mangole.trolling.customMobs.CustomMobEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeadlySpider extends CustomMob {

    public DeadlySpider(Trolling trolling) {
        super(trolling, "deadly_spider", Spider.class);
    }

    @CustomMobEvent
    public void onSpiderAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Spider spider = (Spider) this.mob;

        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 5));
        spider.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 5));
    }
}
