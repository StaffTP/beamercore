package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.chat.C;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PortalListener implements Listener {

    @EventHandler
    public void onPortalToNether(PlayerPortalEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && event.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL) {
            Location netherSpawn = HCF.getInstance().getLocationsFile().getNetherSpawn();

            if (netherSpawn != null) {
                Location to = event.getTo();
                int minX = to.getBlockX() - 5, minY = to.getBlockY(), minZ = to.getBlockZ() - 5;
                for (int x = minX; x < minX + 10; ++x) {
                    for (int y = minY; y < minY + 6; ++y) {
                        for (int z = minZ; z < minZ + 10; ++z) {
                            Block block = to.getWorld().getBlockAt(x, y, z), platformBlock = to.getWorld().getBlockAt(x, (int) to.getY(), z);
                            if (platformBlock.getType() != Material.OBSIDIAN)
                                platformBlock.setType(Material.OBSIDIAN);

                            block.setType(Material.AIR);
                        }
                    }
                }
                event.useTravelAgent(false);
                event.setTo(netherSpawn);
                return;
            }
        }
    }

    @EventHandler
    public void onPortalFromNether(PlayerPortalEvent event) {
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
            return;
//        Location netherSpawn = HCF.getInstance().getLocationsFile().getNetherSpawn();
        Location netherExit = HCF.getInstance().getLocationsFile().getNetherExit();

        if(netherExit != null && event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if(!(Faction.getByLocation(event.getFrom()) instanceof SafezoneFaction))
                return;

            event.useTravelAgent(false);
            event.setTo(netherExit);
            return;
        }

//        if(netherSpawn != null && event.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL)
//            event.setTo(netherSpawn);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortalToEnd(PlayerPortalEvent event) {
        if(event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL)
            return;

        Location endSpawn = HCF.getInstance().getLocationsFile().getEndSpawn();
        Location endExit = HCF.getInstance().getLocationsFile().getEndExit();

        event.useTravelAgent(false);

        if(endSpawn != null && event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled(true);
            HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
            if(profile.hasTimer(PlayerTimerType.COMBAT)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(endSpawn);
                    event.getPlayer().setFoodLevel(20);
                }
            }.runTaskLater(HCF.getInstance(), 2L);
            return;
        }

        if(endExit != null && event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.setCancelled(true);
                    event.getPlayer().teleport(endExit);
                }
            }.runTaskLater(HCF.getInstance(), 10L);
        }
    }

    @EventHandler
    public void onPortalFromEnd(PlayerMoveEvent event) {
        if(event.getTo().getBlockX() == event.getFrom().getBlockX()
                && event.getTo().getBlockY() == event.getFrom().getBlockY()
                && event.getTo().getBlockZ() == event.getFrom().getBlockZ())
            return;

        Player player = event.getPlayer();
        if(player.getWorld().getEnvironment() != World.Environment.THE_END)
            return;

        Block block = player.getLocation().getBlock();
        if(block.getType() == Material.ENDER_PORTAL || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.WATER)) {
            Location endExit = HCF.getInstance().getLocationsFile().getEndExit();
            if(endExit == null)
                return;

            player.teleport(endExit);
        }
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    public void onPortal(PlayerPortalEvent event) {
//        Location to = event.getTo();
//
//        if(to.getWorld().getEnvironment() != World.Environment.NETHER)
//            return;
//        if(Faction.getByLocation(to) instanceof SafezoneFaction)
//            return;
//
//        int minX = to.getBlockX() - 5, minY = to.getBlockY(), minZ = to.getBlockZ() - 5;
//        for(int x = minX; x < minX + 10; ++x) {
//            for(int y = minY; y < minY + 6; ++y) {
//                for(int z = minZ; z < minZ + 10; ++z) {
//                    Block block = to.getWorld().getBlockAt(x, y, z), platformBlock = to.getWorld().getBlockAt(x, (int) to.getY(), z);
//                    if(platformBlock.getType() != Material.OBSIDIAN)
//                        platformBlock.setType(Material.OBSIDIAN);
//
//                    block.setType(Material.AIR);
//                }
//            }
//        }
//
//        to.setX(to.getBlockX() + 0.5);
//        to.setZ(to.getBlockZ() + 0.5);
//        to.setY(to.getBlockY() + 2.0);
//
//        event.setTo(to);
//    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block north = event.getBlock().getRelative(BlockFace.NORTH), east = event.getBlock().getRelative(BlockFace.EAST),
                west = event.getBlock().getRelative(BlockFace.WEST), south = event.getBlock().getRelative(BlockFace.SOUTH);

        if(north.getType().equals(Material.PORTAL) || east.getType().equals(Material.PORTAL) || west.getType().equals(Material.PORTAL) || south.getType().equals(Material.PORTAL)) {
            player.sendMessage(C.color("&cYou cannot place that block here."));
            event.setCancelled(true);
        }
    }

    // Idk what this does, but it works <3
    private static List<Block> blocksFromTwoPoints(Location locationOne, Location locationTwo) {
        List<Block> blocks = new ArrayList<>();
        int topBlockX = (locationOne.getBlockX() < locationTwo.getBlockX()) ? locationTwo.getBlockX() : locationOne.getBlockX(), bottomBlockX = (locationOne.getBlockX() > locationTwo.getBlockX()) ? locationTwo.getBlockX() : locationOne.getBlockX();
        int topBlockY = (locationOne.getBlockY() < locationTwo.getBlockY()) ? locationTwo.getBlockY() : locationOne.getBlockY(), bottomBlockY = (locationOne.getBlockY() > locationTwo.getBlockY()) ? locationTwo.getBlockY() : locationOne.getBlockY();
        int topBlockZ = (locationOne.getBlockZ() < locationTwo.getBlockZ()) ? locationTwo.getBlockZ() : locationOne.getBlockZ(), bottomBlockZ = (locationOne.getBlockZ() > locationTwo.getBlockZ()) ? locationTwo.getBlockZ() : locationOne.getBlockZ();
        for(int x = bottomBlockX; x <= topBlockX; ++x) {
            for(int z = bottomBlockZ; z <= topBlockZ; ++z) {
                for(int y = bottomBlockY; y <= topBlockY; ++y) {
                    Block block = locationOne.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP && event.getClickedBlock().getType() == Material.OBSIDIAN) {
            if(event.getItem().getType() != Material.FLINT_AND_STEEL)
                return;

            Location block = new Location(event.getPlayer().getWorld(), (double) (event.getClickedBlock().getX() - 1), (double) (event.getClickedBlock().getY() + 1), (double) event.getClickedBlock().getZ());
            if(event.getPlayer().getWorld().getBlockAt(block).getType() == Material.OBSIDIAN) {
                double block2 = event.getClickedBlock().getLocation().getY() + 1.0;
                double block3 = event.getClickedBlock().getLocation().getX();
                double x1 = event.getClickedBlock().getZ() + 1;
                double z1 = event.getClickedBlock().getLocation().getY() + 3.0;
                double y6 = event.getClickedBlock().getLocation().getX() + 1.0;
                double x2 = event.getClickedBlock().getZ() + 1;
                Location z2 = new Location(event.getPlayer().getWorld(), block3, block2, x1);
                Location z3 = new Location(event.getPlayer().getWorld(), y6, z1, x2);
                for(Block loc12 : blocksFromTwoPoints(z2, z3))
                    loc12.setType(Material.AIR);

                double loc13 = event.getClickedBlock().getLocation().getY() + 1.0;
                double loc14 = event.getClickedBlock().getLocation().getX();
                double x3 = event.getClickedBlock().getZ() - 1;
                double z4 = event.getClickedBlock().getLocation().getY() + 3.0;
                double y7 = event.getClickedBlock().getLocation().getX() + 1.0;
                double x4 = event.getClickedBlock().getZ() - 1;
                Location z5 = new Location(event.getPlayer().getWorld(), loc14, loc13, x3);
                Location z6 = new Location(event.getPlayer().getWorld(), y7, z4, x4);
                for(Block loc16 : blocksFromTwoPoints(z5, z6))
                    loc16.setType(Material.AIR);
            } else {
                Location block4 = new Location(event.getPlayer().getWorld(), (double) (event.getClickedBlock().getX() + 1), (double) (event.getClickedBlock().getY() + 1), (double) event.getClickedBlock().getZ());
                if(event.getPlayer().getWorld().getBlockAt(block4).getType() == Material.OBSIDIAN) {
                    double block5 = event.getClickedBlock().getLocation().getY() + 1.0;
                    double y8 = event.getClickedBlock().getLocation().getX();
                    double x5 = event.getClickedBlock().getZ() + 1;
                    double z7 = event.getClickedBlock().getLocation().getY() + 3.0;
                    double y9 = event.getClickedBlock().getLocation().getX() - 1.0;
                    double x6 = event.getClickedBlock().getZ() + 1;
                    Location z3 = new Location(event.getPlayer().getWorld(), y8, block5, x5);
                    Location loc17 = new Location(event.getPlayer().getWorld(), y9, z7, x6);
                    for(Block loc19 : blocksFromTwoPoints(z3, loc17))
                        loc19.setType(Material.AIR);

                    double loc20 = event.getClickedBlock().getLocation().getY() + 1.0;
                    double y10 = event.getClickedBlock().getLocation().getX();
                    double x7 = event.getClickedBlock().getZ() - 1;
                    double z8 = event.getClickedBlock().getLocation().getY() + 3.0;
                    double y11 = event.getClickedBlock().getLocation().getX() - 1.0;
                    double x8 = event.getClickedBlock().getZ() - 1;
                    Location z6 = new Location(event.getPlayer().getWorld(), y10, loc20, x7);
                    Location loc21 = new Location(event.getPlayer().getWorld(), y11, z8, x8);
                    for(Block loc23 : blocksFromTwoPoints(z6, loc21))
                        loc23.setType(Material.AIR);
                } else {
                    Location block6 = new Location(event.getPlayer().getWorld(), (double) event.getClickedBlock().getX(), (double) (event.getClickedBlock().getY() + 1), (double) (event.getClickedBlock().getZ() - 1));
                    if(event.getPlayer().getWorld().getBlockAt(block6).getType() == Material.OBSIDIAN) {
                        double block3 = event.getClickedBlock().getLocation().getY() + 1.0;
                        double x1 = event.getClickedBlock().getLocation().getX() + 1.0;
                        double z1 = event.getClickedBlock().getZ();
                        double y6 = event.getClickedBlock().getLocation().getY() + 3.0;
                        double x2 = event.getClickedBlock().getLocation().getX() + 1.0;
                        double z9 = event.getClickedBlock().getZ() + 1;
                        Location loc17 = new Location(event.getPlayer().getWorld(), x1, block3, z1);
                        Location loc24 = new Location(event.getPlayer().getWorld(), x2, y6, z9);
                        for(Block loc25 : blocksFromTwoPoints(loc17, loc24))
                            loc25.setType(Material.AIR);

                        double loc14 = event.getClickedBlock().getLocation().getY() + 1.0;
                        double x3 = event.getClickedBlock().getLocation().getX() - 1.0;
                        double z4 = event.getClickedBlock().getZ();
                        double y7 = event.getClickedBlock().getLocation().getY() + 3.0;
                        double x4 = event.getClickedBlock().getLocation().getX() - 1.0;
                        double z10 = event.getClickedBlock().getZ() + 1;
                        Location loc21 = new Location(event.getPlayer().getWorld(), x3, loc14, z4);
                        Location loc26 = new Location(event.getPlayer().getWorld(), x4, y7, z10);
                        for(Block loc27 : blocksFromTwoPoints(loc21, loc26))
                            loc27.setType(Material.AIR);
                    } else {
                        Location block7 = new Location(event.getPlayer().getWorld(), (double) event.getClickedBlock().getX(), (double) (event.getClickedBlock().getY() + 1), (double) (event.getClickedBlock().getZ() + 1));
                        if(event.getPlayer().getWorld().getBlockAt(block7).getType() == Material.OBSIDIAN) {
                            double y8 = event.getClickedBlock().getLocation().getY() + 1.0;
                            double x5 = event.getClickedBlock().getLocation().getX() + 1.0;
                            double z7 = event.getClickedBlock().getZ();
                            double y9 = event.getClickedBlock().getLocation().getY() + 3.0;
                            double x6 = event.getClickedBlock().getLocation().getX() + 1.0;
                            double z11 = event.getClickedBlock().getZ() - 1;
                            Location loc24 = new Location(event.getPlayer().getWorld(), x5, y8, z7);
                            Location loc28 = new Location(event.getPlayer().getWorld(), x6, y9, z11);
                            for(Block y13 : blocksFromTwoPoints(loc24, loc28))
                                y13.setType(Material.AIR);

                            double y10 = event.getClickedBlock().getLocation().getY() + 1.0;
                            double x7 = event.getClickedBlock().getLocation().getX() - 1.0;
                            double z8 = event.getClickedBlock().getZ();
                            double y11 = event.getClickedBlock().getLocation().getY() + 3.0;
                            double x8 = event.getClickedBlock().getLocation().getX() - 1.0;
                            double z12 = event.getClickedBlock().getZ() - 1;
                            Location loc26 = new Location(event.getPlayer().getWorld(), x7, y10, z8);
                            Location loc29 = new Location(event.getPlayer().getWorld(), x8, y11, z12);
                            for(Block blocks2 : blocksFromTwoPoints(loc26, loc29))
                                blocks2.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}
