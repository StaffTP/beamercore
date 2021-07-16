package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class LogoutCommand {

    @Command(label = "logout", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(!profile.hasTimer(PlayerTimerType.LOGOUT)) {
            PlayerTimer logout = new PlayerTimer(p, PlayerTimerType.LOGOUT);
            logout.add();

            p.sendMessage(Locale.TIMER_LOGOUT.toString());
        } else {
            p.sendMessage(Locale.TIMER_LOGOUT_RUNNING.toString());
        }
    }
}
