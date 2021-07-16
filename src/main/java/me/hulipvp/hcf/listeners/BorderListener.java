package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.LocUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BorderListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(LocUtils.checkBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.BORDER_CANNOT_BUILD.toString());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(LocUtils.checkBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.BORDER_CANNOT_BUILD.toString());
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if(LocUtils.checkBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.BORDER_CANNOT_BUILD.toString());
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if(LocUtils.checkBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.BORDER_CANNOT_BUILD.toString());
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if(LocUtils.checkBorder(event.getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.getTo().getBlockX() == event.getFrom().getBlockX()
                && event.getTo().getBlockY() == event.getFrom().getBlockY()
                && event.getTo().getBlockZ() == event.getFrom().getBlockZ())
            return;

        if(LocUtils.checkBorder(event.getTo())) {
            event.getPlayer().teleport(event.getFrom());
            event.getPlayer().sendMessage(Locale.BORDER_REACHED.toString());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(LocUtils.checkBorder(event.getTo())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Locale.BORDER_REACHED.toString());
        }
    }
}
