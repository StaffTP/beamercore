package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.ChatMode;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionChatArgument {

    @Command(label = "f.chat", aliases = { "f.c" }, playerOnly = true)
    public void onCommand(CommandData args) {
        if(args.length() < 1 || args.length() > 2)
            return;

        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        ChatMode mode;
        if(args.length() <= 1) {
            mode = ChatMode.getNext(profile.getChatMode());
            if (mode.equals(ChatMode.ALLY) && profile.getFactionObj().getAllies().size() == 0)
                mode = ChatMode.getNext(mode);
        } else {
            mode = ChatMode.getByString(args.getArg(1));
        }

        if(mode == null) {
            p.sendMessage(Locale.COMMAND_FACTION_CHAT_INVALID.toString().replace("%type%", args.getArg(1)));
            return;
        }

        PlayerFaction pf = profile.getFactionObj();
        if(pf == null && mode != ChatMode.PUBLIC) {
            profile.setChatMode(ChatMode.PUBLIC);
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        profile.setChatMode(mode);
        p.sendMessage(Locale.COMMAND_FACTION_CHAT_CHANGED.toString().replace("%mode%", mode.getDisplay()));
    }
}
