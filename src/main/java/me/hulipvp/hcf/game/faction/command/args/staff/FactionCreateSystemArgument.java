package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SystemFaction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionCreateSystemArgument {

    @Command(label = "f.createsystem", aliases = {"f.cs"}, permission = "command.createsystem", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() == 2) {
            if(StringUtils.checkFactionName(args.getArg(1)) != null) {
                p.sendMessage(StringUtils.checkFactionName(args.getArg(1)));
            } else {
                SystemFaction sf = new SystemFaction(null, args.getArg(1));
                Faction.getFactions().put(sf.getUuid().toString(), sf);
                HCF.getInstance().getBackend().createFaction(sf);
                p.sendMessage(Locale.COMMAND_FACTION_CREATED.toString());
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "createsystem <name>"));
        }
    }
}
