package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Placeholders;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/9/2021 / 1:17 PM
 * vhcf / me.hulipvp.hcf.commands.admin
 */
public class RefillStationCommand {


    @Command(label = "refillstation")
    public void onCommand(CommandData args) {
        CommandSender sender = args.getSender();
        if (sender.hasPermission("hcf.admin")) {

           Player player = (Player) sender;


            ItemStack station = new ItemStack(Material.BREWING_STAND_ITEM);
            ItemMeta stationMeta = station.getItemMeta();
            stationMeta.setDisplayName(CC.translate("&2&lRefillStation"));
            stationMeta.setLore(CC.translate(Arrays.asList("", "&7Place to create a refill station!", "")));
            station.setItemMeta(stationMeta);

            player.getInventory().addItem(station);
            player.sendMessage(CC.translate("&aYou have given yourself a refill station!"));




        } else {
            sender.sendMessage(CC.translate("&cNo Permission!"));
        }

    }


}
