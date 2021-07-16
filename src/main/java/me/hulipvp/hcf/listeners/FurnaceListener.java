package me.hulipvp.hcf.listeners;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.ConfigValues;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FurnaceListener implements Listener {

    int burnTime = ConfigValues.LISTENERS_COOK_SPEED_MULTIPLIER;

    @EventHandler
    public void onBurn(FurnaceSmeltEvent e) {
        Furnace furnace = (Furnace) e.getBlock().getState();
        (new BukkitRunnable() {
            public void run() {
                if (furnace.getInventory().getSmelting() != null && !(furnace.getCookTime() > 190)) {
                    furnace.setCookTime((short)(furnace.getCookTime() + burnTime));
                    furnace.update();
                } else {
                    cancel();
                }
            }
        }).runTaskTimerAsynchronously(HCF.getInstance(), 2L, 2L);
    }

}
