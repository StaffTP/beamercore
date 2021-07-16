package me.hulipvp.hcf.game.faction.command.args.player.coleader;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionWithdrawArgument {

    @Command(label = "f.withdraw", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {
            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();
                if(!(pf.getMembers().get(p.getUniqueId()).getRank().getRank() >= 2)) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_COLEADER.toString());
                } else {
                    if(args.getArg(1).equalsIgnoreCase("all")) {
                        if(pf.getBalance() > 0) {
                            p.sendMessage(Locale.COMMAND_FACTION_WITHDRAW_SUCCESS.toString().replace("%amount%", String.valueOf(pf.getBalance())));
                            pf.sendMessage(Locale.COMMAND_FACTION_WITHDREW.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()).replace("%amount%", String.valueOf(pf.getBalance()))));

                            profile.setBalance(profile.getBalance() + pf.getBalance());
                            pf.setBalance(0);
                            pf.save();
                            profile.save();
                        } else {
                            p.sendMessage(Locale.COMMAND_FACTION_WITHDRAW_CANNOT.toString());
                        }
                    } else {
                        try {
                            int amount = Integer.parseInt(args.getArg(1));
                            if(amount > 0) {
                                if(pf.getBalance() >= amount) {
                                    p.sendMessage(Locale.COMMAND_FACTION_WITHDRAW_SUCCESS.toString().replace("%amount%", String.valueOf(amount)));
                                    pf.sendMessage(Locale.COMMAND_FACTION_WITHDREW.toString().replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()).replace("%amount%", String.valueOf(amount))));
                                    profile.setBalance(profile.getBalance() + amount);
                                    pf.setBalance(pf.getBalance() - amount);
                                    pf.save();
                                    profile.save();
                                } else {
                                    p.sendMessage(Locale.COMMAND_FACTION_CANNOT_AFFORD.toString());
                                }
                            } else {
                                p.sendMessage(Locale.COMMAND_FACTION_WITHDRAW_CANNOT.toString());
                            }
                        } catch(NumberFormatException e) {
                            p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
                        }
                    }
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "withdraw <amount;all>"));
        }

    }

}
