package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.utils.command.Completer;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FactionHelpArgument {

    private static final List<String> FACTION_COMPLETE = Arrays.asList(
            "announcement",
            "ally",
            "captain",
            "chat",
            "claim",
            "coleader",
            "create",
            "deposit",
            "disband",
            "focus",
            "help",
            "invite",
            "invites",
            "kick",
            "leave",
            "leader",
            "list",
            "lives",
            "location",
            "map",
            "open",
            "rename",
            "revive",
            "sethome",
            "show",
            "stuck",
            "subclaim",
            "unally",
            "unclaim",
            "uninvite",
            "withdraw"
    );

    private static final List<String> STAFF_COMPLETE = Arrays.asList(
            "bypass",
            "claimfor",
            "createsystem",
            "forcedisband",
            "forcejoin",
            "forceleader",
            "forcesethome",
            "resetclaims",
            "setcolor",
            "setdeathban",
            "setdtr",
            "setregen",
            "tp",
            "teleport"
    );

    @Command(label = "f", aliases = {"f.help"}, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        for(String str : HCF.getInstance().getMessagesFile().getFactionHelp())
            p.sendMessage(C.color(str.replace("%primary%", ConfigValues.SERVER_PRIMARY).replace("%secondary%", ConfigValues.SERVER_SECONDARY).replace("%servername%", ConfigValues.SERVER_NAME).replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase()).replace("%website%", ConfigValues.SERVER_WEBSITE).replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK).replace("%store%", ConfigValues.SERVER_STORE)));
    }

    @Completer(label = "f", aliases = { "faction", "t", "team" })
    public List<String> onComplete(CommandData args) {
        if(args.length() <= 1) {

            List<String> toReturn = new ArrayList<>(FACTION_COMPLETE);
            Player player = args.getPlayer();
            if (player.hasPermission("hcf.staff"))
                toReturn.addAll(STAFF_COMPLETE);

            return sortList(toReturn, args.length() == 1 ? args.getArg(0) : "");
        } else {
            return null;
        }
    }

    public static List<String> sortList(List<String> list, String argument) {
        if(argument == null || argument.equals("")) {
            Collections.sort(list);
            return list;
        }

        return list.stream()
                .filter(string -> StringUtils.startsWithIgnoreCase(string, argument))
                .sorted()
                .collect(Collectors.toList());
    }
}
