package me.hulipvp.hcf.listeners.refillstation;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/9/2021 / 1:21 PM
 * vhcf / me.hulipvp.hcf.listeners.refillstation
 */
public class RefillListener implements Listener {

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInHand().getType() != Material.BREWING_STAND_ITEM) {
            return;
        }

        if (player.getInventory().getItemInHand().getItemMeta().getLore() == null) {
            return;
        }

        if (!player.getInventory().getItemInHand().getItemMeta().getLore().equals(CC.translate(Arrays.asList("", "&7Place to create a refill station!", "")))) {
            return;
        }

        event.getBlock().setMetadata("refill", new FixedMetadataValue(HCF.getInstance(), player.getUniqueId()));
        HCF.getInstance().getConfig().set("RefillStations.Locations.X", event.getBlock().getX());
        HCF.getInstance().getConfig().set("RefillStations.Locations.Y", event.getBlock().getY());
        HCF.getInstance().getConfig().set("RefillStations.Locations.Z", event.getBlock().getZ());
        HCF.getInstance().reloadConfig();
        HCF.getInstance().saveConfig();

    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        try {

            if (event.getClickedBlock().getType() == Material.BREWING_STAND) {

                int x = HCF.getInstance().getConfig().getInt("RefillStations.Locations.X");
                int y = HCF.getInstance().getConfig().getInt("RefillStations.Locations.Y");
                int z = HCF.getInstance().getConfig().getInt("RefillStations.Locations.Z");

                if (x == event.getClickedBlock().getX() && y == event.getClickedBlock().getY() && z == event.getClickedBlock().getZ()) {
                    Inventory i = Bukkit.createInventory(null, 36, CC.translate("&2&lRefill"));
                    i.setItem(0, new ItemStack(Material.ENDER_PEARL, 16));
                    i.setItem(1, new ItemStack(Material.BAKED_POTATO, 64));
                    i.setItem(7, new ItemStack(Material.POTION, 1, (short) 8226));
                    i.setItem(8, new ItemStack(Material.POTION, 1, (short) 8226));
                    i.setItem(16, new ItemStack(Material.POTION, 1, (short) 8226));
                    i.setItem(17, new ItemStack(Material.POTION, 1, (short) 8226));
                    i.setItem(25, new ItemStack(Material.POTION, 1, (short) 8226));
                    i.setItem(26, new ItemStack(Material.POTION, 1, (short) 8226));
                    i.setItem(34, new ItemStack(Material.GOLD_SWORD));
                    i.setItem(35, new ItemStack(Material.GOLD_SWORD));
                    for (int j = 0; j < i.getSize(); j++) {
                        i.addItem(new ItemStack(Material.POTION, 1, (short) 16421));
                    }
                    event.setCancelled(true);
                    player.openInventory(i);


                }


            }
        }catch (NullPointerException e) {
            //do nothing
        }

    }

}
