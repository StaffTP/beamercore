package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionMapArgument {

    @Command(label = "f.map", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getMapLocation() != null) {
            profile.hideMap();
            p.sendMessage(Locale.COMMAND_FACTION_MAP_HIDDEN.toString());
        } else {
            profile.updateMap(p.getLocation());
            if(profile.getMapPillars() == null || profile.getMapPillars().isEmpty()) {
                profile.setMapLocation(null);
                p.sendMessage(C.color(Locale.COMMAND_FACTION_MAP_NONE_NEARBY.toString()));
                return;
            }

            p.sendMessage(Locale.COMMAND_FACTION_MAP_SHOWN.toString());
        }
    }
}
