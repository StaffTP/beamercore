package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockCommand {

    @Command(label = "block", permission = "command.block", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if (args.getArgs().length == 0) {
            if (p.getItemInHand().getType() == Material.IRON_INGOT)
            {
                int amount = p.getItemInHand().getAmount();
                Material m = p.getItemInHand().getType();
                if (amount > 8)
                {
                    int total = amount / 9;
                    int remainder = amount % 9;
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_BLOCK, total) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(m, remainder) });
                }
            }
            else if (p.getItemInHand().getType() == Material.GOLD_INGOT)
            {
                int amount = p.getItemInHand().getAmount();
                Material m = p.getItemInHand().getType();
                if (amount > 8)
                {
                    int total = amount / 9;
                    int remainder = amount % 9;
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_BLOCK, total) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(m, remainder) });
                }
            }
            else if (p.getItemInHand().getType() == Material.COAL)
            {
                int amount = p.getItemInHand().getAmount();
                Material m = p.getItemInHand().getType();
                if (amount > 8)
                {
                    int total = amount / 9;
                    int remainder = amount % 9;
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COAL_BLOCK, total) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(m, remainder) });
                }
            }
            else if (p.getItemInHand().getType() == Material.EMERALD)
            {
                int amount = p.getItemInHand().getAmount();
                Material m = p.getItemInHand().getType();
                if (amount > 8)
                {
                    int total = amount / 9;
                    int remainder = amount % 9;
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.EMERALD_BLOCK, total) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(m, remainder) });
                }
            }
            else if (p.getItemInHand().getType() == Material.DIAMOND)
            {
                int amount = p.getItemInHand().getAmount();
                Material m = p.getItemInHand().getType();
                if (amount > 8)
                {
                    int total = amount / 9;
                    int remainder = amount % 9;
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.DIAMOND_BLOCK, total) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(m, remainder) });
                }
            }
            else if (p.getItemInHand().getType() == Material.QUARTZ)
            {
                int amount = p.getItemInHand().getAmount();
                Material m = p.getItemInHand().getType();
                if (amount > 3)
                {
                    int total = amount / 4;
                    int remainder = amount % 4;
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR, 1));
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.QUARTZ_BLOCK, total) });
                    p.getInventory().addItem(new ItemStack[] { new ItemStack(m, remainder) });
                }
            }
        }
    }
}
