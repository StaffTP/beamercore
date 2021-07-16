package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MeltCommand {

    @Command(label = "melt", permission = "command.melt", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if (args.getArgs().length == 0) {
            if (p.getItemInHand().getType() == Material.IRON_ORE)
            {
                int amount = p.getItemInHand().getAmount();
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_INGOT, amount) });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
            else if (p.getItemInHand().getType() == Material.GOLD_ORE)
            {
                int amount = p.getItemInHand().getAmount();
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_INGOT, amount) });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
        }
    }
}
