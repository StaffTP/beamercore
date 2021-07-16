package me.hulipvp.hcf.listeners;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GlassListener implements Listener {

    @Getter private static Map<String, List<Location>> rendered = new HashMap<>();

//    @EventHandler
//    public void onMove(PlayerMoveEvent e) {
//        handleMove(e.getPlayer(), e);
//    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
            handleMove(e.getPlayer(), e.getTo(), e.getFrom());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitTask task = HCFProfile.getWallBorderTask().remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HCFProfile.getWallBorderTask().put(player.getUniqueId(), new WarpTimerRunnable(this, player).runTaskTimerAsynchronously(HCF.getInstance(), 20L, 20L));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeGlass(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        removeGlass(e.getPlayer());
    }

    private void handleMove(Player player, Location to, Location from) {
        if(to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ())
            return;
        if(Math.abs(from.getX() - to.getX()) == 0.5 && Math.abs(from.getZ() - to.getZ()) == 0.5)
            return; // zSpigot's weird movement
        if((from.getX() - 0.5 == to.getX() && from.getZ() - 0.5 == to.getZ()) || (from.getX() + 0.5 == to.getX() && from.getZ() + 0.5 == to.getZ()))
            return;

        TaskUtils.runAsync(() -> {
            HCFProfile profile = HCFProfile.getByPlayer(player);
            boolean hasCombat = profile.hasTimer(PlayerTimerType.COMBAT) && profile.getTimerByType(PlayerTimerType.COMBAT).getTimeRemaining() > 0, hasPvpTimer = profile.hasTimer(PlayerTimerType.PVPTIMER);
            if(hasCombat || hasPvpTimer) {
                List<Faction> nearby = Claim.getNearbyFactions(to, ConfigValues.FACTIONS_CLAIM_GLASS_BUFFER);
                if(nearby == null || nearby.isEmpty())
                    return;

                for(Faction faction : nearby) {
                    if(hasPvpTimer && faction instanceof PlayerFaction) {
                        Faction factionTo = Faction.getByLocation(to), factionFrom = Faction.getByLocation(from);
                        TaskUtils.runSync(() -> {
                            if(factionTo != factionFrom && factionTo == faction) {
                                from.setX(from.getBlockX() + 0.5);
                                from.setZ(from.getBlockZ() + 0.5);
                                player.teleport(from);
                            } else if(factionTo == factionFrom && factionTo == faction) {
                                teleportOut(player, Faction.getClaimAt(from));
                            }

                            for (Claim claim : faction.getClaims())
                                renderGlass(claim, player, to);
                        });
                        continue;
                    }

                    if(!hasCombat)
                        continue;

                    if(!faction.isDeathban() || faction.getType() == FactionType.SAFEZONE || faction instanceof SafezoneFaction) {
                        Faction factionTo = Faction.getByLocation(to), factionFrom = Faction.getByLocation(from);
                        TaskUtils.runSync(() -> {
                            if(factionTo != factionFrom && factionTo == faction) {
                                from.setX(from.getBlockX() + 0.5);
                                from.setZ(from.getBlockZ() + 0.5);
                                player.teleport(from);
                            } else if(factionTo == factionFrom && factionTo == faction) {
                                teleportOut(player, Faction.getClaimAt(from));
                            }

                            faction.getClaims().forEach(claim -> {
                                renderGlass(claim, player, to);
                            });
                        });
                    }
                }
            } else {
                TaskUtils.runSync(() -> {
                    removeGlass(player);
                });
            }
        });
    }

    public void teleportOut(Player player, Claim claim) {
        if(claim == null)
            return;

        Cuboid cuboid = claim.toCuboid();
//        Location location = new Location(cuboid.getWorld(), cuboid.getLowerX() - 1, 0, cuboid.getLowerZ() - 1);
        Location location = cuboid.getWorld().getHighestBlockAt(new Location(cuboid.getWorld(), cuboid.getLowerX() - 1, 0, cuboid.getLowerZ() - 1)).getLocation();
        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            player.teleport(location.add(0, 1, 0));
        }, 1L);
    }

    public static boolean hasGlass(Player player) {
        return rendered.containsKey(player.getUniqueId().toString()) && !rendered.get(player.getUniqueId().toString()).isEmpty();
    }

    public static boolean hasGlass(Player player, Location location) {
        return hasGlass(player) && rendered.get(player.getUniqueId().toString()).contains(location);
    }

    public static void removeGlass(Player player) {
        List<Location> locations = rendered.remove(player.getUniqueId().toString());
        if(locations != null) {
            for (Location location : locations) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }
    }

    private void renderGlass(Player player, List<Location> locations) {
        if(!rendered.containsKey(player.getUniqueId().toString()))
            rendered.put(player.getUniqueId().toString(), new ArrayList<>());

        Iterator<Location> it = rendered.get(player.getUniqueId().toString()).iterator();
        while(it.hasNext()) {
            Location location = it.next();
            if(locations.contains(location))
                continue;
            if(!(Faction.getByLocation(location) instanceof SafezoneFaction))
                continue;

            Block block = location.getBlock();
            player.sendBlockChange(location, block.getTypeId(), block.getData());
            it.remove();
        }

        locations.forEach(location -> {
            player.sendBlockChange(location, 95, (byte) ((int) ConfigValues.FACTIONS_WALL_COLOR));
        });
        rendered.get(player.getUniqueId().toString()).addAll(locations);
    }

    private void renderGlass(Claim claim, Player player, Location to) {
        int closerX = closestNumber(to.getBlockX(), claim.getCorner1().getBlockX(), claim.getCorner2().getBlockX());
        int closerZ = closestNumber(to.getBlockZ(), claim.getCorner1().getBlockZ(), claim.getCorner2().getBlockZ());

        boolean updateX = Math.abs(to.getX() - closerX) < ConfigValues.FACTIONS_CLAIM_GLASS_BUFFER;
        boolean updateZ = Math.abs(to.getZ() - closerZ) < ConfigValues.FACTIONS_CLAIM_GLASS_BUFFER;
        if(!updateX && !updateZ)
            return;

        List<Location> toUpdate = new LinkedList<>();
        if(updateX) {
            for(int y = -5; y < 5; ++y) {
                for(int x = -5; x < 5; ++x) {
                    Location location;
                    if(isInBetween(claim.getCorner1().getBlockZ(), claim.getCorner2().getBlockZ(), to.getBlockZ() + x)) {
                        if(!toUpdate.contains(location = new Location(to.getWorld(), (double) closerX, (double) (to.getBlockY() + y), (double) (to.getBlockZ() + x)))) {
                            if(location.getBlock().getType() == Material.AIR || !location.getBlock().getType().isSolid())
                                toUpdate.add(location);
                        }
                    }
                }
            }
        }

        if(updateZ) {
            for(int y = -5; y < 5; ++y) {
                for(int x = -5; x < 5; ++x) {
                    Location location;
                    if(isInBetween(claim.getCorner1().getBlockX(), claim.getCorner2().getBlockX(), to.getBlockX() + x) && !toUpdate.contains(location = new Location(to.getWorld(), (double) (to.getBlockX() + x), (double) (to.getBlockY() + y), (double) closerZ))) {
                        if(location.getBlock().getType() == Material.AIR || !location.getBlock().getType().isSolid())
                            toUpdate.add(location);
                    }
                }
            }
        }

        if(!toUpdate.isEmpty())
            renderGlass(player, toUpdate);
    }

    private boolean isInBetween(int xone, int xother, int mid) {
        return Math.abs(xone - xother) == Math.abs(mid - xone) + Math.abs(mid - xother);
    }

    private int closestNumber(int from, int... numbers) {
        int distance = Math.abs(numbers[0] - from);
        int idx = 0;

        for(int c = 1; c < numbers.length; ++c) {
            int cdistance = Math.abs(numbers[c] - from);
            if(cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return numbers[idx];
    }

    private static class WarpTimerRunnable extends BukkitRunnable {

        private GlassListener listener;
        private Player player;

        private Location lastLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);

        public WarpTimerRunnable(GlassListener listener, Player player) {
            this.listener = listener;
            this.player = player;
        }

        @Override
        public void run() {
            Location location = player.getLocation();

            // Check if the player moved or is AFK.
            double x = location.getBlockX();
            double y = location.getBlockY();
            double z = location.getBlockZ();
            if (this.lastLocation.getX() == x && this.lastLocation.getY() == y && this.lastLocation.getZ() == z) {
                return;
            }

            this.listener.handleMove(player, location, lastLocation);
            this.lastLocation = location;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.listener = null;
            this.player = null;
        }
    }
}
