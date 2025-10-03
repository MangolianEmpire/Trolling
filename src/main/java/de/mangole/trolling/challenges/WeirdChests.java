package de.mangole.trolling.challenges;

import de.mangole.trolling.Trolling;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.ContainerUtils;
import de.mangole.trolling.utils.CustomChallenge;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class WeirdChests extends CustomChallenge {

    public WeirdChests(Trolling trolling) {
        super(trolling, "WeirdChests");
    }

    @Override
    protected void onActivate() {
    }

    @Override
    protected void onDeactivate() {
    }

    @ChallengeEvent
    public void onChestOpen(PlayerInteractEvent event) {
        if (!isActive()) return;
        if (event.getClickedBlock() == null) return;
        Block chest = event.getClickedBlock();
        World world = chest.getWorld();

        if (chest.getType() != Material.CHEST) return;
        if (!event.getAction().isRightClick()) return;

        double random = new Random().nextDouble();

        if (random < 0.1) {
            event.setCancelled(true);
            world.createExplosion(chest.getLocation(), 10.0f);
        } else if (random < 0.2) {
            event.setCancelled(true);
            ArrayList<Block> surroundingBlocks = new ArrayList<>();

            for (int x = chest.getX() - 2; x <= chest.getX() + 2; x++) {
                for (int z = chest.getZ() - 2; z <= chest.getZ() + 2; z++) {
                    for (int y = chest.getY() - 2; y <= chest.getY() + 2; y++) {
                        if (x != 0 && z != 0 && y != 0) {
                            surroundingBlocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }

            world.spawnParticle(Particle.PORTAL, chest.getLocation(), 5);
            world.playSound(chest.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            int randomBlock = new Random().nextInt(surroundingBlocks.size());
            ContainerUtils.swapBlockContainers(chest, surroundingBlocks.get(randomBlock), trolling);
        }
    }

    @ChallengeEvent
    public void onBarrelOpen(InventoryOpenEvent event) {
        Random random = new Random();
        if (random.nextDouble() > 0.01) return;
        Inventory inv = event.getInventory();
        if (inv.getLocation() != null) {
            var block = inv.getLocation().getBlock();
            if (block.getType() == Material.BARREL || block.getType() == Material.CHEST){

                event.setCancelled(true);

                Zombie mimicZombie = (Zombie) block.getWorld().spawnEntity(block.getLocation(), EntityType.ZOMBIE);
                mimicZombie.setBaby();
                mimicZombie.setInvisible(true);
                mimicZombie.customName(Component.text("Mimic"));
                mimicZombie.setCustomNameVisible(false);
                mimicZombie.setShouldBurnInDay(false);
                Player player = (Player) event.getPlayer();
                mimicZombie.setTarget(player);

                ArmorStand barrelStand = (ArmorStand) block.getWorld()
                        .spawnEntity(block.getLocation().add(0,0,0), EntityType.ARMOR_STAND);
                barrelStand.setInvisible(true);
                barrelStand.setGravity(false);
                barrelStand.setMarker(true);
                barrelStand.getEquipment().setHelmet(new ItemStack(block.getType()));

                block.breakNaturally();

                new BukkitRunnable(){
                    @Override
                    public void run(){
                        if (!mimicZombie.isDead()){
                            barrelStand.teleport(mimicZombie.getLocation().add(0,-1,0));
                        } else {
                            barrelStand.remove();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(trolling, 0L, 1L);
            }
        }
    }




}
