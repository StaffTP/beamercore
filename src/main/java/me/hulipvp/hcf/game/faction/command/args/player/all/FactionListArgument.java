package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.TaskUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionListArgument {

    @Command(label = "f.list", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        List<UUID> factions = PlayerFaction.getSorted();
        Map<UUID, Integer> listedFactions = new HashMap<>();

        int currentPage = 1;
        if(args.length() == 2) {
            try {
                currentPage = Integer.parseInt(args.getArg(1));
            } catch(NumberFormatException e) {
                p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
                return;
            }
        }

        int pages = (factions.size() / 10) + 1;
        if(currentPage < 0 || currentPage > pages) {
            p.sendMessage(Locale.COMMAND_FACTION_LIST_INVALID_PAGE.toString());
            return;
        }

        int index = 0;
        for(UUID pfUuid : factions)
            listedFactions.put(pfUuid, index++);

        int starting = (currentPage - 1) * 10;
        sendList(p, currentPage, pages, starting, listedFactions);
    }

    private void sendList(Player p, int currentPage, int pages, int starting, Map<UUID, Integer> facs) {
        TaskUtils.runAsync(() -> {
            int index = 0;

            List<Map.Entry<UUID, Integer>> entries = new ArrayList<>(facs.entrySet());
            entries.sort((entry1, entry2) -> {
                PlayerFaction pf1 = (PlayerFaction) Faction.getFaction(entry1.getKey()), pf2 = (PlayerFaction) Faction.getFaction(entry2.getKey());

                return pf2.getOnlineCount() - pf1.getOnlineCount();
            });

            p.sendMessage(C.color("&7&m----------------------------------------------------"));
            p.sendMessage(C.color("&9Faction List &7(Page " + currentPage + "/" + pages + ")"));
            for(Map.Entry<UUID, Integer> entry : entries) {
                if(index++ < starting || index > starting + 10)
                    continue;

                PlayerFaction pf = PlayerFaction.getPlayerFaction(entry.getKey());

                if (pf.getOnlinePlayers().size() == 0)
                    continue;

                p.sendMessage(C.color("&7" + index + ". " + ChatColor.YELLOW + pf.getRelationColor(p) + pf.getName() + " &a(" + pf.getOnlineCount() + "/" + pf.getMembers().size() + ")"));
            }
            if (index == 0)
                p.sendMessage(C.color("&cNo factions"));

            p.sendMessage(C.color("&7You are currently on &fPage " + currentPage + "/" + pages + "&7."));
            p.sendMessage(C.color("&7To view other pages, use &e/f list <page>&7."));
            p.sendMessage(C.color("&7&m----------------------------------------------------"));
        });
    }
}
