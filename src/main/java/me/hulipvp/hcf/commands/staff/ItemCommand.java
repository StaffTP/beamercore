package me.hulipvp.hcf.commands.staff;

import com.google.common.base.Joiner;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.TaskUtils;
import me.hulipvp.hcf.utils.item.Enchantments;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemCommand {

    @Command(label = "enchant", aliases = { "ench" }, permission = "command.enchant", playerOnly = true)
    public void onEnchant(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() < 2) {
            player.sendMessage(C.color("&cUsage: /" + args.getLabel() + " <enchant> <level>"));
            return;
        }

        ItemStack item = player.getItemInHand();
        if(item == null || item.getType() == Material.AIR) {
            player.sendMessage(C.color("&cYou must be holding an item to enchant."));
            return;
        }

        Enchantment enchantment = Enchantments.getByName(args.getArg(0));
        if(enchantment == null) {
            player.sendMessage(C.color("&cThe enchantment '" + args.getArg(0) + "' does not exist."));
            return;
        }

        if(!StringUtils.isInt(args.getArg(1))) {
            player.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
            return;
        }

        int level = Integer.parseInt(args.getArg(1));
        if(level <= 0) {
            player.sendMessage(C.color("&cPlease use a positive number."));
            return;
        }

        TaskUtils.runSync(() -> {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);

            player.setItemInHand(item);
            player.updateInventory();

            player.sendMessage(C.color("&eAdded " + enchantment.getName() + " level " + level + " to your held item."));
        });
    }

    @Command(label = "rename", permission = "command.rename", playerOnly = true)
    public void onRename(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() <= 0) {
            player.sendMessage(C.color("&cUsage: /" + args.getLabel() + " <name;remove>"));
            return;
        }

        ItemStack item = player.getItemInHand();
        if(item == null || item.getType() == Material.AIR) {
            player.sendMessage(C.color("&cYou must be holding an item to rename."));
            return;
        }

        String name = Joiner.on(' ').join(args.getArgs());

        TaskUtils.runSync(() -> {
            boolean remove = name.replace(" ", "").equals("remove");
            
            ItemMeta meta = item.getItemMeta();
            if(remove)
                meta.setDisplayName(null);
            else
                meta.setDisplayName(C.color(name));
            item.setItemMeta(meta);

            player.setItemInHand(item);
            player.updateInventory();

            if(remove)
                player.sendMessage(C.color("&eRemoved the name of your item."));
            else
                player.sendMessage(C.color("&eChanged the name of your item to '" + C.color(name) + "&e'."));
        });
    }

    @Command(label = "craft", permission = "command.craft", playerOnly = true)
    public void onCraft(CommandData args) {
        Player player = args.getPlayer();
        player.openWorkbench(null, true);
    }
}
