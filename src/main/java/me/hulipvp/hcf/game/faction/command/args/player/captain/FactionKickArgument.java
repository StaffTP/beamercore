package me.hulipvp.hcf.game.faction.command.args.player.captain;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Duration;

public class FactionKickArgument {

    @Command(label = "f.kick", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {

            if(profile.getFactionObj() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            } else {
                PlayerFaction pf = profile.getFactionObj();

                if(!(pf.getMembers().get(p.getUniqueId()).getRank().getRank() >= 2)) {
                    p.sendMessage(Locale.COMMAND_FACTION_NOT_CAPTAIN.toString());
                } else {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                    if(target.isOnline() || target.hasPlayedBefore()) {
                        if(pf.getMembers().containsKey(target.getUniqueId())) {
                            FactionMember member = pf.getMembers().get(target.getUniqueId());

                            if(member.getRank().getRank() < pf.getMembers().get(p.getUniqueId()).getRank().getRank()) {
                                pf.removeMember(target.getUniqueId());
                                HCFProfile targetProfile = HCFProfile.getByUuid(target.getUniqueId());
                                targetProfile.setFaction(null);
                                targetProfile.save();

                                if(pf.getDtr() > pf.getMaxDtr()) {
                                    pf.setDtr(pf.getMaxDtr());
                                }

                                p.sendMessage(C.color("&eYou have successfully kicked &d" + target.getName()));
                                LunarClientAPI api = LunarClientAPI.getInstance();
                                if(target.isOnline())
                                    api.sendTitle((Player) target, TitleType.TITLE, CC.translate("&cYou have been kicked from &d" + pf.getName() + "&c!"), Duration.ofSeconds(3));
                                ((Player) target).sendMessage(C.color("&cYou have been kicked from your faction."));
                            } else {
                                p.sendMessage(C.color("&cYou may not kick this player. (:"));
                            }
                        } else {
                            p.sendMessage(Locale.COMMAND_FACTION_PLAYER_NOT_IN.toString().replace("%name%", args.getArg(1)));
                        }
                    } else {
                        p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    }
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "kick <name>"));
        }
    }

}
