package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class BrewingListener implements Listener {

    private List<BrewingStand> brewingStands = new ArrayList<>();

    public BrewingListener() {
        Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), () -> {
            if(ConfigValues.LISTENERS_BREWING_SPEED_MULTIPLIER <= 1)
                return;

            for(BrewingStand stand : brewingStands) {
                if(stand.getLocation().getChunk().isLoaded()) {
                    if(stand.getBrewingTime() > 1)
                        stand.setBrewingTime(Math.max(1, stand.getBrewingTime() - ConfigValues.LISTENERS_BREWING_SPEED_MULTIPLIER));
                }
            }
        }, 2L, 2L);
    }

    @EventHandler
    public void onBrew(BrewEvent e) {
        BrewingStand brewingStand = (BrewingStand) e.getBlock().getState();
        if(!(brewingStands.contains(brewingStand)))
            brewingStands.add(brewingStand);
    }

    @EventHandler
    public void onClickBlock(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock().getType() != Material.BREWING_STAND) return;

        BrewingStand brewingStand = (BrewingStand) e.getClickedBlock().getState();
        if(!(brewingStands.contains(brewingStand)))
            brewingStands.add(brewingStand);
    }
}
