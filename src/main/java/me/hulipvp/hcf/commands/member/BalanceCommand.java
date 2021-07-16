package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BalanceCommand {

    @Command(label = "balance", aliases = {"money", "$", "bal"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() != 1) {
            p.sendMessage(Locale.COMMAND_BALANCE.toString().replace("%balance%", String.valueOf(profile.getBalance())));
        } else {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if(target == null) {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            HCFProfile targetProfile = HCFProfile.getByPlayer(target);
            p.sendMessage(Locale.COMMAND_BALANCE_OTHER.toString().replace("%player%", target.getName()).replace("%balance%", String.valueOf(targetProfile.getBalance())));
        }
    }
}
