package me.hulipvp.hcf.commands.member;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PayCommand {

    @Command(label = "pay", playerOnly = true)
    public void onPayCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() != 2) {
            p.sendMessage(Locale.COMMAND_PAY_USAGE.toString());
        } else {
            Player target = Bukkit.getPlayer(args.getArg(0));
            String rawInt = args.getArg(1);
            if(target == null) {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            if(!StringUtils.isInt(rawInt)) {
                p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                return;
            }

            HCFProfile targetProfile = HCFProfile.getByPlayer(target);
            int amount = Integer.valueOf(rawInt);
            if(amount <= 0 || profile.getBalance() < amount) {
                p.sendMessage(Locale.COMMAND_PAY_NOT_ENOUGH.toString());
                return;
            }

            profile.removeFromBalance(amount);
            targetProfile.addToBalance(amount);

            profile.save();
            targetProfile.save();
            p.sendMessage(Locale.COMMAND_PAY_SENT.toString().replace("%recipient%", target.getName()).replace("%amount%", String.valueOf(amount)));
            target.sendMessage(Locale.COMMAND_PAY_RECEIVED.toString().replace("%sender%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()).replace("%amount%", String.valueOf(amount))));
        }
    }
}
