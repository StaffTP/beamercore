package me.hulipvp.hcf.listeners.player;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.faction.normal.FactionClaimEvent;
import me.hulipvp.hcf.game.faction.Claim;
import me.hulipvp.hcf.game.faction.Cuboid;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.FactionType;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.LocUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClaimingListener implements Listener {

    private static String WILDERNESS = Locale.FACTION_WILDERNESS.toString() + " " + Locale.FACTION_DEATHBAN.toString();

    private Map<UUID, ClaimTask> currentTasks;

    public ClaimingListener() {
        currentTasks = new HashMap<>();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
            Faction fac = Faction.getByLocation(event.getPlayer().getLocation());
            if (fac != null) {
                if (profile.getClaimData().getCurrentTerritory() != null)
                    if (profile.getClaimData().getCurrentTerritory().toString().equals(fac.getUuid().toString()))
                        return;

                if (fac instanceof SafezoneFaction) {
                    if (!profile.hasTimer(PlayerTimerType.COMBAT)) {
                        TaskUtils.runSync(() -> {
                            event.getPlayer().setFoodLevel(20);
                            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                        });
                    }
                }

                Faction current;
                if (profile.getClaimData().getCurrentTerritory() != null) {
                    if (Faction.getFaction(profile.getClaimData().getCurrentTerritory()) != null) {
                        current = Faction.getFaction(profile.getClaimData().getCurrentTerritory());
                    } else {
                        current = null;
                    }
                } else {
                    current = null;
                }

                if (fac.getType() == FactionType.PLAYER) {
                    if (profile.hasTimer(PlayerTimerType.PVPTIMER)) {
                        event.setTo(event.getFrom());
                        return;
                    }
                }

                if (fac instanceof PlayerFaction) {
                    PlayerFaction pf = (PlayerFaction) fac;
                    String display = (current == null) ? WILDERNESS : current.getDisplayString();
                    if (!display.equals(WILDERNESS))
                        if (current instanceof PlayerFaction)
                            display = PlayerFaction.getPlayerFaction(profile.getClaimData().getCurrentTerritory()).getDisplayString(event.getPlayer().getUniqueId());

                    event.getPlayer().sendMessage(Locale.FACTION_LEAVING.toString().replace("%faction%", C.color(display)));
                    event.getPlayer().sendMessage(Locale.FACTION_ENTERING.toString().replace("%faction%", C.color(pf.getDisplayString(event.getPlayer().getUniqueId()))));
                } else {
                    String display = (current == null) ? WILDERNESS : current.getDisplayString();
                    if (!display.equals(WILDERNESS))
                        if (current instanceof PlayerFaction)
                            display = ((PlayerFaction) current).getDisplayString(event.getPlayer().getUniqueId());

                    event.getPlayer().sendMessage(Locale.FACTION_LEAVING.toString().replace("%faction%", C.color(display)));
                    event.getPlayer().sendMessage(Locale.FACTION_ENTERING.toString().replace("%faction%", C.color(fac.getDisplayString())));
                }

                profile.getClaimData().setCurrentTerritory(fac.getUuid());
            } else {
                if (profile.getClaimData().getCurrentTerritory() == null)
                    return;

                Faction current = Faction.getFaction(profile.getClaimData().getCurrentTerritory());
                String display = (current == null) ? WILDERNESS : current.getDisplayString();
                if (!display.equals(WILDERNESS))
                    if (current instanceof PlayerFaction)
                        display = ((PlayerFaction) current).getDisplayString(event.getPlayer().getUniqueId());

                event.getPlayer().sendMessage(Locale.FACTION_LEAVING.toString().replace("%faction%", C.color(display)));
                event.getPlayer().sendMessage(Locale.FACTION_ENTERING.toString().replace("%faction%", C.color(WILDERNESS)));
                profile.getClaimData().setCurrentTerritory(null);
            }
            if (event.getPlayer().getAllowFlight() && !event.getPlayer().hasPermission("hcf.staff")) {
                if (fac == null) {
                    event.getPlayer().setAllowFlight(false);
                    event.getPlayer().setFlying(false);
                    event.getPlayer().sendMessage(Locale.COMMAND_FLY_DISABLED.toString());
                } else if ((profile.getFactionObj() != null && !profile.getFactionObj().equals(fac))
                        || !fac.getName().equalsIgnoreCase("Spawn")) {
                    event.getPlayer().setAllowFlight(false);
                    event.getPlayer().setFlying(false);
                    event.getPlayer().sendMessage(Locale.COMMAND_FLY_DISABLED.toString());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        onMove(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem())
            return;
        if (!event.getItem().isSimilar(LocUtils.getClaimingWand()))
            return;

        HCFProfile profile = HCFProfile.getByPlayer(event.getPlayer());
        if (!profile.getClaimData().isClaiming())
            return;
        /*if(profile.hasTimer(PlayerTimerType.PVPTIMER)) {
            event.getPlayer().sendMessage(Locale.TIMER_CANNOT_CLAIM.toString());
            return;
        }*/

        if (ServerTimer.isEotw()) {
            event.getPlayer().sendMessage(Locale.COMMAND_EOTW_CANNOT_CLAIM.toString());
            return;
        }

        if (!event.getPlayer().isOp() && event.getAction().name().contains("BLOCK")) {
            PlayerFaction pf = profile.getFactionObj();
            if (pf != null && pf.isRaidable() && profile.getClaimData().getClaimingFaction().equals(pf.getUuid())) {
                pf.sendMessage(Locale.CLAIMING_RAIDABLE.toString());
                return;
            }
        }

        Faction fac = Faction.getFaction(profile.getClaimData().getClaimingFaction());
        event.setCancelled(true);

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                if (event.getPlayer().isSneaking()) {
                    this.handleClaiming(profile, event.getPlayer(), fac);
                } else {
                    Location loc = event.getClickedBlock().getLocation();
                    loc.setY(0.0);

                    if (!this.checkClaimLocation(loc, profile, event.getPlayer()))
                        return;

                    profile.getClaimData().setPos1(loc);
                    profile.getClaimData().updatePillars(event.getPlayer());
                    event.getPlayer().sendMessage(Locale.CLAIMING_SETPOS1.toString());
                    this.handleClaimCost(fac, profile, event.getPlayer());
                }
                break;
            case LEFT_CLICK_AIR:
                if (event.getPlayer().isSneaking())
                    this.handleClaiming(profile, event.getPlayer(), fac);
                break;
            case RIGHT_CLICK_BLOCK:
                Location loc = event.getClickedBlock().getLocation();
                loc.setY(256.0);

                if (!this.checkClaimLocation(loc, profile, event.getPlayer()))
                    return;

                profile.getClaimData().setPos2(loc);
                profile.getClaimData().updatePillars(event.getPlayer());
                event.getPlayer().sendMessage(Locale.CLAIMING_SETPOS2.toString());
                this.handleClaimCost(fac, profile, event.getPlayer());
                break;
            case RIGHT_CLICK_AIR:
                this.clearClaims(profile);
                event.getPlayer().sendMessage(Locale.CLAIMING_CLEARED.toString());
                break;
        }
    }

    private void clearClaims(HCFProfile profile) {
        profile.getClaimData().setPos1(null);
        profile.getClaimData().setPos2(null);
        profile.getClaimData().removePillars(Bukkit.getPlayer(profile.getUuid()));
        profile.getClaimData().setPillars(new ArrayList<>());
    }

    private void handleClaiming(HCFProfile profile, Player player, Faction fac) {
        Location corner1 = profile.getClaimData().getPos1(), corner2 = profile.getClaimData().getPos2();
        if (corner1 == null || corner2 == null || !corner1.getWorld().getUID().equals(corner2.getWorld().getUID())) {
            player.sendMessage(Locale.CLAIMING_NOT_SELECTED.toString());
            return;
        }

        if (currentTasks.containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.CLAIMING_ALREADY_STARTED.toString());
            return;
        }

        Claim claim = new Claim(profile.getClaimData().getPos1(), profile.getClaimData().getPos2());
        Cuboid claimCube = claim.toCuboid();
        if (claimCube.getSizeX() < 5 || claimCube.getSizeZ() < 5) {
            player.sendMessage(Locale.CLAIMING_INVALID_SIZE.toString());
            return;
        }

        if (fac instanceof PlayerFaction && fac.getClaims().size() >= 1) {
            player.sendMessage(Locale.CLAIMING_ALREADY_CLAIMED.toString());
            return;
        }

//        return;
        ClaimTask claimTask = new ClaimTask(player, claimCube, fac);
        claimTask.runTaskAsynchronously(HCF.getInstance());
        currentTasks.put(player.getUniqueId(), claimTask);

        Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), () -> {
            while (claimTask.running)
                continue;

            if (!claimTask.canClaim)
                return;

            FactionClaimEvent event = new FactionClaimEvent(fac, player, claim);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.sendMessage(Locale.CLAIMING_CANNOT_CLAIM_HERE.toString());
                return;
            }

            if (fac instanceof PlayerFaction && profile.hasFaction() && profile.getFactionObj().equals(fac)) {
                PlayerFaction pf = (PlayerFaction) fac;
                int cost = claim.getPrice(false);

                if (pf.getBalance() >= cost) {
                    pf.setBalance(pf.getBalance() - cost);
                } else {
                    player.sendMessage(Locale.CLAIMING_CANNOT_AFFORD.toString());
                    return;
                }
            }

            fac.addClaim(claim);
            fac.save();
            profile.getClaimData().setClaiming(false);
            profile.getClaimData().setClaimingFaction(null);
            clearClaims(profile);

            player.sendMessage(Locale.CLAIMING_COMPLETED.toString());
            Bukkit.getScheduler().runTask(HCF.getInstance(), () -> {
                if (player.getInventory().contains(LocUtils.getClaimingWand())) {
                    player.getInventory().remove(LocUtils.getClaimingWand());
                    player.updateInventory();
                }
            });
        }, 10L);
    }

    private boolean checkClaimLocation(Location loc, HCFProfile profile, Player p) {
        Faction claimingFaction = Faction.getFaction(profile.getClaimData().getClaimingFaction());
        Faction fac = Faction.getByLocation(loc);
        if (fac != null) {
            if (claimingFaction instanceof PlayerFaction && !fac.getUuid().equals(profile.getClaimData().getClaimingFaction())) {
                p.sendMessage(Locale.CLAIMING_CANNOT_CLAIM_HERE.toString());
                return false;
            }
        }

        if (LocUtils.checkBorder(loc)) {
            p.sendMessage(Locale.CLAIMING_CANNOT_CLAIM_BORDER.toString());
            return false;
        }

        if (claimingFaction instanceof PlayerFaction) {
            if (Claim.isNearby(loc, ConfigValues.FACTIONS_CLAIM_BUFFER)) {
                p.sendMessage(Locale.CLAIMING_CANNOT_CLAIM_HERE.toString());
                return false;
            }
        }

        if (!p.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            if (!p.isOp()) {
                p.sendMessage(Locale.CLAIMING_CANNOT_CLAIM_HERE.toString());
                return false;
            }
        }

        return true;
    }

    private void handleClaimCost(Faction fac, HCFProfile profile, Player p) {
        if (profile.getClaimData().getPos1() == null || profile.getClaimData().getPos2() == null)
            return;

        Claim claim = new Claim(profile.getClaimData().getPos1(), profile.getClaimData().getPos2());
        int cost = claim.getPrice(false);

        if (fac instanceof PlayerFaction && profile.hasFaction() && profile.getFactionObj().equals(fac.getUuid())) {
            p.sendMessage(Locale.CLAIMING_COST.toString().replace("%cost%", C.color(((((PlayerFaction) fac).getBalance() < cost) ? "&c" : "&a") + "$" + cost)).replace("%sizeX%", claim.toCuboid().getSizeX() + "").replace("%sizeZ%", claim.toCuboid().getSizeZ() + ""));
        } else {
            if (profile.getFactionObj().getBalance() < cost) {
                p.sendMessage(Locale.CLAIMING_COST.toString().replace("%cost%", C.color("&c$" + cost)).replace("%sizeX%", claim.toCuboid().getSizeX() + "").replace("%sizeZ%", claim.toCuboid().getSizeZ() + ""));

            } else {
                p.sendMessage(Locale.CLAIMING_COST.toString().replace("%cost%", C.color("&a$" + cost)).replace("%sizeX%", claim.toCuboid().getSizeX() + "").replace("%sizeZ%", claim.toCuboid().getSizeZ() + ""));

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropWand(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().isSimilar(LocUtils.getClaimingWand())) {
            HCFProfile profile = HCFProfile.getByPlayer(e.getPlayer());
            if (profile.getClaimData().isClaiming()) {
                if (e.getPlayer().getInventory().contains(LocUtils.getClaimingWand()))
                    e.getPlayer().getInventory().remove(LocUtils.getClaimingWand());

                e.getPlayer().sendMessage(Locale.CLAIMING_ENDED.toString());
                profile.getClaimData().setClaiming(false);
                profile.getClaimData().setClaimingFaction(null);
                profile.getClaimData().setPos1(null);
                profile.getClaimData().setPos2(null);
                profile.getClaimData().removePillars(e.getPlayer());
                e.getItemDrop().remove();
            } else {
                e.getItemDrop().remove();
            }
        }
    }

    private class ClaimTask extends BukkitRunnable {

        public boolean canClaim, running;

        private Player player;
        private Cuboid claimCube;
        private Faction fac;

        public ClaimTask(Player player, Cuboid claimCube, Faction fac) {
            this.player = player;
            this.claimCube = claimCube;
            this.fac = fac;

            this.running = true;
            this.canClaim = true;
        }

        @Override
        public void run() {
            player.sendMessage(Locale.CLAIMING_STARTED.toString());
            if (fac instanceof PlayerFaction) {
                if (claimCube.getChunks().size() > 32) {
                    player.sendMessage(Locale.CLAIMING_INVALID_SIZE.toString());
                    canClaim = false;
                    running = false;
                    cancel();
                    return;
                }

                for (Faction faction : Claim.getNearbyFactions(player.getLocation(), claimCube.getSizeX() + 10)) {
                    if (faction.getName().equals(fac.getName()))
                        continue;
                    if (faction.getClaims().isEmpty())
                        continue;

                    for (Claim otherClaim : faction.getClaims()) {
                        Cuboid fCube = otherClaim.toCuboid();

                        List<Block> blocks = new ArrayList<>();
                        blocks.addAll(fCube.getFace(Cuboid.CuboidDirection.North).getBlocks());
                        blocks.addAll(fCube.getFace(Cuboid.CuboidDirection.East).getBlocks());
                        blocks.addAll(fCube.getFace(Cuboid.CuboidDirection.South).getBlocks());
                        blocks.addAll(fCube.getFace(Cuboid.CuboidDirection.West).getBlocks());

                        for (Block block : blocks) {
                            if (claimCube.contains(block.getLocation())) {
                                canClaim = false;
                                running = false;
                                player.sendMessage(Locale.CLAIMING_CANNOT_CLAIM_HERE.toString());
                                cancel();
                                return;
                            }
                        }
                    }
                }
            }

            running = false;
            cancel();
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            currentTasks.remove(player.getUniqueId());
        }
    }
}
