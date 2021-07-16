package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionSubclaimArgument {

    @Command(label = "f.subclaim", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        for(String str : HCF.getInstance().getMessagesFile().getFactionSubclaiming())
            p.sendMessage(C.color(str.replace("%primary%", ConfigValues.SERVER_PRIMARY).replace("%secondary%", ConfigValues.SERVER_SECONDARY).replace("%servername%", ConfigValues.SERVER_NAME).replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase()).replace("%website%", ConfigValues.SERVER_WEBSITE).replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK).replace("%store%", ConfigValues.SERVER_STORE)));
    }
}
