package me.hulipvp.hcf.game.faction.command.args.player.leader;

import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FactionOpenArgument {

    @Command(label = "f.open", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
        } else {
            PlayerFaction pf = profile.getFactionObj();
            if(!pf.getLeader().getUuid().equals(p.getUniqueId())) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_LEADER.toString());
                return;
            }

            pf.setOpen(!pf.isOpen());
            if(pf.isOpen() && !Locale.COMMAND_FACTION_OPEN.toString().isEmpty())
                Bukkit.broadcastMessage(Locale.COMMAND_FACTION_OPEN.toString().replace("%faction%", pf.getName()));
            else if(!pf.isOpen() && !Locale.COMMAND_FACTION_CLOSED.toString().isEmpty())
                Bukkit.broadcastMessage(Locale.COMMAND_FACTION_CLOSED.toString().replace("%faction%", pf.getName()));
        }
    }
}
