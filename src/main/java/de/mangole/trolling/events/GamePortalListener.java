package de.mangole.trolling.events;

import de.mangole.trolling.Trolling;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GamePortalListener implements Listener {

    private final Trolling trolling;

    public GamePortalListener(Trolling trolling) {
        this.trolling = trolling;
    }

    @EventHandler
    public void onNetherPortal(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;

        Player player = event.getPlayer();
        World currentWorld = player.getWorld();

        if (currentWorld.getName().equals("game_overworld")) {
            World nether = Bukkit.getWorld("game_nether");
            if (nether != null) {
                event.setTo(handleCustomPortal(player, currentWorld, nether, 1.0 / 8.0));
            }
        } else if (currentWorld.getName().equals("game_nether")) {
            World overworld = Bukkit.getWorld("game_overworld");
            if (overworld != null) {
                event.setTo(handleCustomPortal(player, currentWorld, overworld, 8.0));
            }
        }
    }

    @EventHandler
    public void onEndPortal(PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) return;

        Player player = event.getPlayer();
        World currentWorld = player.getWorld();

        if (currentWorld.getName().equals("game_overworld")) {
            World end = Bukkit.getWorld("game_end");
            if (end != null) {
                Location target = end.getSpawnLocation();
                event.setTo(target);
            }
        } else if (currentWorld.getName().equals("game_end")) {
            World overworld = Bukkit.getWorld("game_overworld");
            if (overworld != null) {
                Location target = overworld.getSpawnLocation();
                event.setTo(target);
            }
        }
    }


    private Location handleCustomPortal(Player player, World fromWorld, World toWorld, double scale) {
        Location from = player.getLocation();

        // Skaliere die Koordinaten
        double x = from.getX() * scale;
        double y = from.getY();
        double z = from.getZ() * scale;

        Location target = new Location(toWorld, x, y, z);

        int search_radius = toWorld.getName().equals("game_overworld") ? 128 : 16;

        // Suche Portal in Zielwelt
        Location portal = getNearestPortal(toWorld, target, search_radius);
        if (portal == null) {
            portal = buildAndLinkPortal(toWorld, target);
        }

        return portal;
    }

    private Location buildAndLinkPortal(World world, Location target) {
        // Finde eine solide Fläche in der Nähe (z.B. Boden)
        Location base = target.clone();

        int x = base.getBlockX();
        int y = base.getBlockY();
        int z = base.getBlockZ();

        for (int i = -1; i < 5; i++) {
            for (int j = -1; j < 6; j++) {
                for (int k = -1; k < 2; k++) {
                    world.getBlockAt(x + i, y + j, z + k).setType(j == -1 ? Material.BUDDING_AMETHYST : Material.AIR);
                }
            }
        }
        // 4x5 Portalrahmen bauen
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                boolean frame = (i == 0 || i == 3 || j == 0 || j == 4);
                world.getBlockAt(x + i, y + j, z).setType(frame ? Material.OBSIDIAN : Material.AIR);
            }
        }

        // Portal anzünden
        world.getBlockAt(x + 1, y + 1, z).setType(Material.FIRE);

        // Warte kurz, bis NetherPortal-Blöcke generiert sind
        Bukkit.getScheduler().runTaskLater(Trolling.plugin, () -> {
            Location nearest = getNearestPortal(world, base, 16);
            if (nearest != null) {
                // fertig
            }
        }, 2L);

        return base;
    }

    private Location getNearestPortal(World world, Location target, int searchRadius) {
        Location nearest = null;
        double nearestDist = Double.MAX_VALUE;

        // Suche nach existierenden Portalen
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                for (int dy = -searchRadius; dy <= searchRadius; dy++) {
                    Location check = target.clone().add(dx, dy, dz);
                    if (check.getBlock().getType() == Material.NETHER_PORTAL) {
                        double dist = check.distanceSquared(target);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = check;
                        }
                    }
                }
            }
        }

        if (nearest != null) {
            return nearest.add(0.5, 0.5, 0.5);
        }

        return null;
    }
}
