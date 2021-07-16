package me.hulipvp.hcf.game.faction.command.args.player.member;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class FactionLivesArgument {

    @Command(label = "f.lives", aliases = { "f.depositlives", "f.dl" }, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = args.getPlayer();

        if(args.length() != 2) {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "lives <amount>"));
        } else {
            HCFProfile profile = HCFProfile.getByPlayer(p);
            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
                return;
            }

            PlayerFaction faction = profile.getFactionObj();
            if(profile.getLives() <= 0) {
                p.sendMessage(Locale.COMMAND_FACTION_LIVES_NO_LIVES.toString());
                return;
            }

            if(!StringUtils.isInt(args.getArg(1))) {
                p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(1)));
                return;
            }

            int lives = Integer.parseInt(args.getArg(1));
            if(lives <= 0 || profile.getLives() < lives) {
                p.sendMessage(Locale.COMMAND_FACTION_LIVES_NOT_ENOUGH.toString().replace("%amount%", args.getArg(1)));
                return;
            }

            faction.setLives(faction.getLives() + lives);
            faction.save();

            profile.setLives(profile.getLives() - lives);
            profile.save();

            p.sendMessage(Locale.COMMAND_FACTION_LIVES_SUCCESS.toString().replace("%amount%", args.getArg(1)));
            faction.sendExcludingMessage(p, Locale.COMMAND_FACTION_LIVES_SUCCESS_FACTION.toString().replace("%amount%", args.getArg(1)).replace("%player%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())));
        }
    }
}
