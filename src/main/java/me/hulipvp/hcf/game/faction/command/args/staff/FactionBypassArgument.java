package me.hulipvp.hcf.game.faction.command.args.staff;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionBypassArgument {

    @Command(label = "f.bypass", aliases = {"f.bypass"}, permission = "command.bypass", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        profile.setBypass(!profile.isBypass());

        p.sendMessage(C.color("&eBypass mode has been " + ((profile.isBypass()) ? "&aenabled" : "&cdisabled") + "&e."));
    }
}
