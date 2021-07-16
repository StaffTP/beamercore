package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IngotCommand {

    @Command(label = "ingot", permission = "command.ingot", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if (args.getArgs().length == 0) {
            if (p.getItemInHand().getType() == Material.IRON_BLOCK)
            {
                int amount = p.getItemInHand().getAmount();
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_INGOT, amount * 9) });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
            else if (p.getItemInHand().getType() == Material.GOLD_BLOCK)
            {
                int amount = p.getItemInHand().getAmount();
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_INGOT, amount * 9) });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
            else if (p.getItemInHand().getType() == Material.DIAMOND_BLOCK)
            {
                int amount = p.getItemInHand().getAmount();
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.DIAMOND, amount * 9) });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
            else if (p.getItemInHand().getType() == Material.QUARTZ_BLOCK)
            {
                int amount = p.getItemInHand().getAmount();
                p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.QUARTZ, amount * 4) });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
            else if (p.getItemInHand().getType() == Material.LAPIS_BLOCK)
            {
                int amount = p.getItemInHand().getAmount();
                ItemStack lapiz = new ItemStack(Material.INK_SACK, amount * 9, (short)4);
                p.getInventory().addItem(new ItemStack[] { lapiz });
                p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
            }
            else if (p.getItemInHand().getType() == Material.GOLD_NUGGET)
            {
                int amount = p.getItemInHand().getAmount();
                if (amount >= 9)
                {
                    int remainder = amount % 9;
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_INGOT, amount / 9) });
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_NUGGET, remainder) });
                }
            }
        }
    }
}
