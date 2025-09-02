package de.mangole.trolling.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class ContainerUtils {

    private ContainerUtils() {}

    /** Tauscht zwei Blöcke und erhält (wenn vorhanden) die Inventare beider Container. */
    public static void swapBlockContainers(Block a, Block b, Plugin plugin) {
        // Chunks sicher laden
        ensureChunkLoaded(a);
        ensureChunkLoaded(b);

        // Inhalte sichern (auch DoubleChest berücksichtigen)
        ItemStack[] aContents = getContainerContents(a);
        ItemStack[] bContents = getContainerContents(b);

        // Blocktypen tauschen (ohne Physik, um Race-Conditions zu vermeiden)
        Material aMat = a.getType();
        Material bMat = b.getType();
        setTypeQuiet(a, bMat);
        setTypeQuiet(b, aMat);

        // 1–2 Ticks warten, bis neue Container initialisiert sind
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Inhalte zurückschreiben
            restoreContainerContents(b, aContents); // a’s Inhalt gehört jetzt nach b
            restoreContainerContents(a, bContents); // b’s Inhalt gehört jetzt nach a
        }, 2L);
    }

    /** Holt die (kombinierte) Inventarbelegung eines Container-Blocks; null wenn kein Container. */
    private static ItemStack[] getContainerContents(Block block) {
        BlockState state = block.getState();
        if (!(state instanceof Container container)) return null;

        Inventory inv = container.getInventory();

        // DoubleChest → kombiniertes Inventar verwenden
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof DoubleChest dc) {
            inv = dc.getInventory();
        }
        return inv.getContents().clone();
    }

    /** Schreibt gesicherte Items in den Container am Block zurück (trimmt/padded bei anderer Größe). */
    private static void restoreContainerContents(Block block, ItemStack[] contents) {
        if (contents == null) return;

        BlockState state = block.getState();
        if (!(state instanceof Container container)) return;

        Inventory inv = container.getInventory();
        InventoryHolder holder = inv.getHolder();

        // DoubleChest: live kombiniertes Inventar verwenden
        if (holder instanceof DoubleChest dc) {
            Inventory dcInv = dc.getInventory();
            setFitting(dcInv, contents);
            // update() auf einer Hälfte reicht i. d. R., da TileEntity gemeinsam ist
            container.update(true, false);
            return;
        }

        // Single-Container: falls verfügbar, Snapshot-Inventar benutzen (robuster)
        try {
            // Viele Container (Paper/Spigot) bieten ein Snapshot-Inventory,
            // das zusammen mit update() atomar übernommen wird.
            Inventory snap = container.getSnapshotInventory();
            setFitting(snap, contents);
            container.update(true, false);
        } catch (NoSuchMethodError | UnsupportedOperationException ignored) {
            // Fallback: live Inventory beschreiben
            setFitting(inv, contents);
            container.update(true, false);
        }
    }

    /** Kopiert so viele Items wie passen (bei Größenunterschieden). */
    private static void setFitting(Inventory target, ItemStack[] source) {
        int n = Math.min(target.getSize(), source.length);
        for (int i = 0; i < n; i++) {
            target.setItem(i, source[i]);
        }
        // Rest-Slots bleiben wie sie sind (leer)
    }

    /** Setzt Blocktyp ohne Physik-Updates (wo verfügbar), reduziert Race-Conditions. */
    private static void setTypeQuiet(Block block, Material type) {
        try {
            block.setType(type, false);
        } catch (NoSuchMethodError e) {
            block.setType(type);
        }
    }

    private static void ensureChunkLoaded(Block b) {
        Chunk c = b.getChunk();
        if (!c.isLoaded()) c.load();
    }
}
