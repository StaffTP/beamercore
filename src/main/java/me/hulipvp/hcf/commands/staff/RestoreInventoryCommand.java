package me.hulipvp.hcf.commands.staff;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.Death;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RestoreInventoryCommand {

    @Command(label = "restoreinventory", permission = "command.restore", aliases = {"restoreinv"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() != 1) {
            p.sendMessage(Locale.COMMAND_RESTORE_USAGE.toString());
        } else {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if(target == null) {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            HCFProfile targetProfile = HCFProfile.getByPlayer(target);
            if(targetProfile.getDeaths().size() == 0) {
                p.sendMessage(Locale.COMMAND_RESTORE_NOT_FOUND.toString().replace("%player%", target.getName()));
                return;
            }

            Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                Death death = targetProfile.getDeaths().get(targetProfile.getDeaths().size() - 1);
                target.getInventory().setArmorContents(death.getInv().getArmor());
                target.getInventory().setContents(death.getInv().getItems());
                target.updateInventory();

                p.sendMessage(Locale.COMMAND_RESTORE_SUCCESS.toString().replace("%player%", target.getName()));
                target.sendMessage(Locale.COMMAND_RESTORE_RESTORED.toString().replace("%restorer%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
            }, 1L);
        }
    }
}
