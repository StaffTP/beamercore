package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand {

    @Command(label = "economy", aliases = {"eco"}, permission = "command.economy")
    public void onCommand(CommandData args) {
        CommandSender sender = args.getSender();
        this.sendUsage(sender);
    }

    @Command(label = "economy.add", aliases = {"eco.add", "economy.give", "eco.give"}, permission = "command.economy.add")
    public void onAddArgument(CommandData args) {
        CommandSender sender = args.getSender();

        if(args.length() != 3) {
            this.sendUsage(sender);
        } else {
            Player target = Bukkit.getPlayer(args.getArg(1));
            String rawInt = args.getArg(2);
            if(target == null) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            if(!StringUtils.isInt(rawInt)) {
                sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                return;
            }

            HCFProfile profile = HCFProfile.getByPlayer(target);
            int amount = Integer.valueOf(rawInt);

            profile.addToBalance(amount);
            profile.save();
            sender.sendMessage(Locale.COMMAND_ECONOMY_GIVEN.toString()
                    .replace("%target%", target.getName())
                    .replace("%amount%", String.valueOf(amount))
            );
        }
    }

    @Command(label = "economy.remove", aliases = {"eco.remove"}, permission = "command.economy.remove")
    public void onRemoveArgument(CommandData args) {
        CommandSender sender = args.getSender();

        if(args.length() != 3) {
            this.sendUsage(sender);
        } else {
            Player target = Bukkit.getPlayer(args.getArg(1));
            String rawInt = args.getArg(2);
            if(target == null) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            if(!StringUtils.isInt(rawInt)) {
                sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                return;
            }

            HCFProfile profile = HCFProfile.getByPlayer(target);
            int amount = Integer.valueOf(rawInt);

            profile.removeFromBalance(amount);
            profile.save();
            sender.sendMessage(Locale.COMMAND_ECONOMY_TAKEN.toString()
                    .replace("%target%", target.getName())
                    .replace("%amount%", String.valueOf(amount))
            );
        }
    }

    @Command(label = "economy.set", aliases = {"eco.set"}, permission = "command.economy.set")
    public void onSetArgument(CommandData args) {
        CommandSender sender = args.getSender();

        if(args.length() != 3) {
            this.sendUsage(sender);
        } else {
            Player target = Bukkit.getPlayer(args.getArg(1));
            String rawInt = args.getArg(2);
            if(target == null) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            if(!StringUtils.isInt(rawInt)) {
                sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                return;
            }

            HCFProfile profile = HCFProfile.getByPlayer(target);
            int amount = Integer.valueOf(rawInt);

            profile.setBalance(amount);
            profile.save();
            sender.sendMessage(Locale.COMMAND_ECONOMY_SET.toString()
                    .replace("%player%", target.getName())
                    .replace("%amount%", String.valueOf(amount))
            );
        }
    }

    private void sendUsage(CommandSender sender) {
        for(String str : HCF.getInstance().getMessagesFile().getEconomyHelp())
            sender.sendMessage(C.color(str
                    .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                    .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
            ));
    }
}
