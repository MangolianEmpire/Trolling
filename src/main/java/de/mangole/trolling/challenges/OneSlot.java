package de.mangole.trolling.challenges;

import de.mangole.trolling.GameStatus;
import de.mangole.trolling.Trolling;
import de.mangole.trolling.events.GameChangeEvent;
import de.mangole.trolling.utils.ChallengeEvent;
import de.mangole.trolling.utils.CustomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class OneSlot extends CustomChallenge {

    Set<Integer> allowedSlots = Set.of(0, 36, 37, 38, 39, 40);

    public OneSlot(Trolling trolling) {
        super(trolling, "One Slot");
    }

    @Override
    protected void onActivate() {

    }

    @Override
    protected void onDeactivate() {

    }

    @ChallengeEvent
    public void onGameStart(GameChangeEvent event) {
        if (event.getOldStatus() == GameStatus.LOBBY && event.getNewStatus() == GameStatus.RUNNING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                blockInventory(player);
            }
        }
    }

    @ChallengeEvent
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        blockInventory(player);
    }

    @ChallengeEvent
    public void onInventoryBlock(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER) return;

        ItemStack current = event.getCurrentItem();
        if (current != null && current.getType() == Material.BARRIER) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) return;

        if (!allowedSlots.contains(event.getSlot())) {
            event.setCancelled(true);
        }
    }


    @ChallengeEvent
    public void onInventoryDrop(PlayerDropItemEvent event) {
        ItemStack current = event.getItemDrop().getItemStack();
        if (current.getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }

    @ChallengeEvent
    public void onPlace(BlockPlaceEvent event) {
        ItemStack current = event.getItemInHand();
        if (current.getType() == Material.BARRIER) {
            event.setCancelled(true);
        }
    }


    @ChallengeEvent
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(drop -> drop.getType() == Material.BARRIER);
    }

    @ChallengeEvent
    public void onRespawn(PlayerRespawnEvent event) {
        blockInventory(event.getPlayer());
    }

    private void blockInventory(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (allowedSlots.contains(i)) continue;
            ItemStack barrier = new ItemStack(Material.BARRIER);
            inventory.setItem(i, barrier);
        }
    }
}
