package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionStuckArgument {

    @Command(label = "f.stuck", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getTimerByType(PlayerTimerType.STUCK) != null) {
            p.sendMessage(Locale.TIMER_STUCK_RUNNING.toString());
        } else {
            PlayerTimer timer = new PlayerTimer(p, PlayerTimerType.STUCK);
            timer.add();

            profile.setStuckLocation(p.getLocation());
            p.sendMessage(Locale.TIMER_STUCK.toString());
        }
    }
}
