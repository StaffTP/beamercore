package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.ExpUtils;
import me.hulipvp.hcf.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BottleCommand {

    @Command(label = "xpbottle", aliases = { "bottle" }, permission = "command.bottle", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = args.getPlayer();
        int currentXp = ExpUtils.getTotalExperience(player);
        if(currentXp <= 25) {
            player.sendMessage(Locale.COMMAND_BOTTLE_NOT_ENOUGH.toString());
            return;
        }

        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Locale.COMMAND_BOTTLE_FULL_INVENTORY.toString());
            return;
        }

        ExpUtils.setTotalExperience(player, 0);

        player.getInventory().addItem(getXpBottle(currentXp, player.getName()));
        player.sendMessage(Locale.COMMAND_BOTTLE_SUCCESS.toString().replace("%amount%", currentXp + ""));
    }

    private static ItemStack getXpBottle(int xpAmount, String enchanter) {
        return new ItemBuilder(Material.EXP_BOTTLE)
                .amount(1)
                .name(C.color("&e&lXP Bottle &7(Throw)"))
                .lore(
                        "&6Amount: " + xpAmount + " XP",
                        "&6Enchanter: " + enchanter
                )
                .get();
    }

    public static boolean isXpBottle(ItemStack item) {
        return item != null && item.getType() == Material.EXP_BOTTLE
                && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).contains("XP");
    }

    public static int getXpAmount(ItemStack item) {
        try {
            return Integer.parseInt(C.strip(item.getItemMeta().getLore().get(0).split(" ")[1].replace("XP", "")));
        } catch(Exception err) {
            return 0;
        }
    }
}
