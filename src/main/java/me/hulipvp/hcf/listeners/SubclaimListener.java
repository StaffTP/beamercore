package me.hulipvp.hcf.listeners;

import lombok.Getter;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

public class SubclaimListener implements Listener {

    @Getter private List<Material> types;
    private static final BlockFace[] SIGN_FACES = new BlockFace[]{
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.UP
    };

    public SubclaimListener() {
        types = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE);
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if(!(holder instanceof Chest))
            return;

        Block block = ((Chest) holder).getBlock();
        e.setCancelled(isSubclaimed(block));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Collection<Sign> signs = getAttachedSigns(block);
        if (event.getBlock().getType().equals(Material.WALL_SIGN)) {
            signs.add((Sign) event.getBlock().getState());
        }
        if(!signs.isEmpty()) {
            Player player = event.getPlayer();
            for(Sign sign : signs) {
                if(sign.getLine(0).contains("[Subclaim]")) {
                    HCFProfile profile = HCFProfile.getByPlayer(player);
                    PlayerFaction faction = profile.getFactionObj();
                    if(faction == null) {
                        event.setCancelled(true);
                        return;
                    }

                    if(faction.isRaidable()) {
                        event.setCancelled(false);
                        return;
                    }

                    FactionMember member = faction.getMembers().get(profile.getUuid());
                    if(member == null) {
                        event.setCancelled(true);
                        return;
                    }

                    boolean isMember = false;
                    if (faction.getMembers().keySet().contains(player.getUniqueId())) {
                        isMember = true;
                    }

                    if (!isMember) {
                        event.setCancelled(true);
                        return;
                    }

                    FactionRank rank = FactionRank.getByString(sign.getLine(1));
                    if(rank != null) {
                        event.setCancelled(!member.isAtLeast(rank));
                    } else {
                        if(checkLinesForName(player.getName(), sign.getLines()) || member.getRank().getRank() > 2)
                            event.setCancelled(false);
                        else
                            event.setCancelled(true);
                    }
                }
            }

            if(event.isCancelled())
                player.sendMessage(Locale.SUBCLAIM_CANNOT_OPEN.toString());
        }
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        Player player = e.getPlayer();
        Sign sign = (Sign) e.getBlock().getState();
        Block block = sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
        if(isSubclaimed(block)) {
            e.getBlock().breakNaturally();
            e.setCancelled(true);
            player.sendMessage(Locale.SUBCLAIM_ALREADY_EXISTS.toString());
            return;
        }

        if(getTypes().contains(block.getType())) {
            if(e.getLine(0).equalsIgnoreCase("[Subclaim]")) {
                HCFProfile profile = HCFProfile.getByPlayer(player);
                if(!profile.hasFaction()) {
                    player.sendMessage(Locale.SUBCLAIM_NEED_FACTION.toString());
                    e.getBlock().breakNaturally();
                    e.setCancelled(true);
                    return;
                }

                PlayerFaction faction = profile.getFactionObj();
                FactionMember member = faction.getMembers().get(profile.getUuid());
                if(!member.isAtLeast(FactionRank.CAPTAIN)) {
                    player.sendMessage(Locale.SUBCLAIM_INVALID_RANK.toString());
                    e.getBlock().breakNaturally();
                    e.setCancelled(true);
                    return;
                }

                Faction territoryFaction = Faction.getByLocation(player.getLocation());
                if(territoryFaction == null || territoryFaction != faction) {
                    player.sendMessage(Locale.SUBCLAIM_INVALID_TERRITORY.toString());
                    e.getBlock().breakNaturally();
                    e.setCancelled(true);
                    return;
                }

                if(FactionRank.getByString(e.getLine(1)) == null && faction.getOnlinePlayers().stream().map(Player::getName).noneMatch(other -> other.equalsIgnoreCase(e.getLine(1)))) {
                    player.sendMessage(Locale.SUBCLAIM_INVALID_MEMBER.toString().replace("%name%", e.getLine(1)));
                    e.getBlock().breakNaturally();
                    e.setCancelled(true);
                    return;
                }

                e.setLine(0, C.color("&9[Subclaim]"));
                e.setLine(1, e.getLine(1));
                e.setLine(2, "");
                e.setLine(3, "");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(!getTypes().contains(block.getType()))
            return;

        Collection<Sign> signs = getAttachedSigns(block);
        if(!signs.isEmpty()) {
            for(Sign sign : signs) {
                if(sign.getLine(0).contains("[Subclaim]")) {
                    HCFProfile profile = HCFProfile.getByPlayer(player);
                    PlayerFaction faction = profile.getFactionObj();
                    if(faction == null) {
                        event.setCancelled(true);
                        return;
                    }

                    if(faction.isRaidable()) {
                        event.setCancelled(false);
                        return;
                    }

                    FactionMember member = faction.getMembers().get(profile.getUuid());
                    if(member == null) {
                        event.setCancelled(true);
                        return;
                    }

                    boolean isMember = false;
                    if (faction.getMembers().keySet().contains(player.getUniqueId())) {
                        isMember = true;
                    }

                    if (!isMember) {
                        event.setCancelled(true);
                        return;
                    }

                    FactionRank rank = FactionRank.getByString(sign.getLine(1));
                    if(rank != null) {
                        event.setCancelled(!member.isAtLeast(rank));
                    } else {
                        if(checkLinesForName(player.getName(), sign.getLines()) || member.getRank().getRank() > 2)
                            event.setCancelled(false);
                        else
                            event.setCancelled(true);
                    }
                }
            }

            if(event.isCancelled())
                player.sendMessage(Locale.SUBCLAIM_CANNOT_OPEN.toString());
        }
    }

    private boolean isSubclaimed(Block block) {
        if(ServerTimer.isEotw())
            return false;

        Collection<Sign> signs = getAttachedSigns(block);
        if(!signs.isEmpty()) {
            for(Sign sign : signs) {
                if(sign.getLine(0).contains("[Subclaim]"))
                    return true;
            }
        }

        return false;
    }

    // Taken from iHCF cuz I'm a skid
    private Collection<Sign> getAttachedSigns(Block block) {
        LinkedHashSet<Sign> results = new LinkedHashSet<>();
        getSignsAround(block, results);

        BlockState state = block.getState();
        if(state instanceof Chest) {
            Inventory chestInventory = ((Chest) state).getInventory();
            if(chestInventory instanceof DoubleChestInventory) {
                DoubleChest doubleChest = ((DoubleChestInventory) chestInventory).getHolder();
                Block left = ((Chest) doubleChest.getLeftSide()).getBlock();
                Block right = ((Chest) doubleChest.getRightSide()).getBlock();
                getSignsAround(left.equals(block) ? right : left, results);
            }
        }

        return results;
    }

    private Set<Sign> getSignsAround(Block block, LinkedHashSet<Sign> results) {
        for(BlockFace face : SIGN_FACES) {
            Block relative = block.getRelative(face);
            BlockState relativeState = relative.getState();
            if(relativeState instanceof Sign) {
                org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) relativeState.getData();
                if(relative.getRelative(materialSign.getAttachedFace()).equals(block))
                    results.add((Sign) relative.getState());
            }
        }

        return results;
    }

    private boolean checkLinesForName(String name, String[] lines) {
        boolean toReturn = false;
        for (String line : lines) {
            if (line.equals(name)) toReturn = true;
        }
        return toReturn;
    }
}
