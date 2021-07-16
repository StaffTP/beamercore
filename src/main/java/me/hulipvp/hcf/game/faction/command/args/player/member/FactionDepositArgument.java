package me.hulipvp.hcf.game.faction.command.args.player.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionDepositArgument {

    @Command(label = "f.deposit", aliases = {"f.d"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            HCFProfile profile = HCFProfile.getByPlayer(p);

            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();

                if(args.getArg(1).equalsIgnoreCase("all")) {
                    if(profile.getBalance() > 0) {
                        int amount = profile.getBalance();
                        pf.setBalance(pf.getBalance() + amount);
                        profile.setBalance(0);
                        profile.save();
                        pf.save();

                        p.sendMessage(Locale.COMMAND_FACTION_DEPOSIT_SUCCESS.toString().replace("%amount%", String.valueOf(amount)));
                        pf.sendMessage(CC.translate("&d" + p.getName() + " &ehas deposited &d" + amount + "&e into the faction balance!"));                    } else {
                        p.sendMessage(Locale.COMMAND_FACTION_DEPOSIT_CANNOT.toString());
                    }
                } else {
                    try {
                        int amount = Integer.parseInt(args.getArg(1));
                        if(amount > 0) {
                            if(profile.getBalance() >= amount) {
                                p.sendMessage(Locale.COMMAND_FACTION_DEPOSIT_SUCCESS.toString().replace("%amount%", String.valueOf(amount)));
                                pf.sendMessage(CC.translate("&d" + p.getName() + " &ehas deposited &d" + amount + "&e into the faction balance!"));
                                pf.setBalance(pf.getBalance() + amount);
                                profile.removeFromBalance(amount);
                                profile.save();
                                pf.save();
                            } else {
                                p.sendMessage(Locale.COMMAND_FACTION_DEPOSIT_CANNOT_AFFORD.toString());
                            }
                        } else {
                            p.sendMessage(Locale.COMMAND_FACTION_DEPOSIT_CANNOT.toString());
                        }
                    } catch(NumberFormatException e) {
                        p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
                    }
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "deposit <amount;all>"));
        }
    }
}
