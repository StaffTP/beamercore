package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    @Command(label = "gmc", aliases = { "gm.c", "gm.1", "gamemode.c", "gamemode.1" }, permission = "command.gamemode.creative", playerOnly = true)
    public void onGamemodeCreative(CommandData args) {
        Player player = args.getPlayer();

        int defaultArguments = 0;
        if (args.length() > 0) {
            if (args.getArg(0).equalsIgnoreCase("c") || args.getArg(0).equalsIgnoreCase("1")) defaultArguments = 1;
        }

        if (args.length() == 0 || (args.length() == 1 && defaultArguments == 1)) {

            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Locale.GAMEMODE_CREATIVE.toString());

        } else if ((args.length() == 1 && defaultArguments == 0) || (args.length() == 2 && defaultArguments == 1)) {

            if (!player.hasPermission("hcf.command.gamemode.creative.others")) {
                player.sendMessage(Locale.NO_PERMISSION.toString());
                return;
            }
            int whichArgument = 0;
            if (defaultArguments == 1) whichArgument = 1;
            Player target = Bukkit.getPlayer(args.getArg(whichArgument));
            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(whichArgument)));
                return;
            }

            target.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Locale.GAMEMODE_CREATIVE_OTHER.toString().replace("%target%", args.getArg(whichArgument)));
            target.sendMessage(Locale.GAMEMODE_CREATIVE_FOR_OTHER.toString().replace("%player%", player.getName()));
        }
    }

    @Command(label = "gms", aliases = { "gm.s", "gm.0", "gamemode.s", "gamemode.0" }, permission = "command.gamemode.survival", playerOnly = true)
    public void onGamemodeSurvival(CommandData args) {
        Player player = args.getPlayer();

        int defaultArguments = 0;
        if (args.length() > 0) {
            if (args.getArg(0).equalsIgnoreCase("s") || args.getArg(0).equalsIgnoreCase("0")) defaultArguments = 1;
        }

        if (args.length() == 0 || (args.length() == 1 && defaultArguments == 1)) {

            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Locale.GAMEMODE_SURVIVAL.toString());

        } else if ((args.length() == 1 && defaultArguments == 0) || (args.length() == 2 && defaultArguments == 1)) {

            if (!player.hasPermission("hcf.command.gamemode.survival.others")) {
                player.sendMessage(Locale.NO_PERMISSION.toString());
                return;
            }
            int whichArgument = 0;
            if (defaultArguments == 1) whichArgument = 1;
            Player target = Bukkit.getPlayer(args.getArg(whichArgument));
            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(whichArgument)));
                return;
            }

            target.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Locale.GAMEMODE_SURVIVAL_OTHER.toString().replace("%target%", args.getArg(whichArgument)));
            target.sendMessage(Locale.GAMEMODE_SURVIVAL_FOR_OTHER.toString().replace("%player%", player.getName()));
        }
    }
}
