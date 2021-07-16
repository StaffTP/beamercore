package me.hulipvp.hcf.game.faction.command.args.player.member;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.ChatMode;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.Placeholders;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class FactionLocationCommand {

    @Command(label = "f.loc", aliases = { "f.location", "tl", "telllocation", "f.tl" }, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
        } else {
            PlayerFaction pf = profile.getFactionObj();

            pf.sendMessage(Placeholders.replacePlaceholders(Locale.CHAT_FORMAT_FACTION.toString().replace("%prefix%", ChatMode.FACTION.getPrefix()).replace("%elo%", Integer.toString(profile.getElo())).replace("%name%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName()).replace("%message%", getCoords(p.getLocation()))), profile.getPlayer(), profile));
        }
    }

    private String getCoords(Location loc) {
        DecimalFormat decimalFormat = TimeUtils.getREMAINING_SECONDS_TRAIL();
        String x = decimalFormat.format(loc.getX());
        String y = decimalFormat.format(loc.getY());
        String z = decimalFormat.format(loc.getZ());
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
