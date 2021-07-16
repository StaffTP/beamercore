package me.hulipvp.hcf.game.faction.command.args.player.all;

import me.activated.core.plugin.AquaCoreAPI;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.faction.normal.FactionCreateEvent;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.Placeholders;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class FactionCreateArgument {

    @Command(label = "f.create", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() == 2) {
            if(profile.getFactionObj() != null) {
                p.sendMessage(Locale.COMMAND_FACTION_ATTEMPT_CREATE.toString());
            } else {
                if(profile.getLastFactionEdit() != 0 && System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1) < profile.getLastFactionEdit()) {
                    p.sendMessage(Locale.COMMAND_FACTION_ACTION_COOLDOWN.toString());
                    return;
                }

                if(StringUtils.checkFactionName(args.getArg(1)) != null) {
                    p.sendMessage(StringUtils.checkFactionName(args.getArg(1)));
                } else {

                    Koth eotw = Koth.getKoth("EOTW");
                    if(eotw != null) {
                        if (eotw.getTimer() > 0) {
                            p.sendMessage(CC.translate("&cYou can't create factions while eotw is active!"));
                            return;
                        }
                    }
                    PlayerFaction pf = new PlayerFaction(null, args.getArg(1), p.getUniqueId());
                    Faction.getFactions().put(pf.getUuid().toString(), pf);
                    PlayerFaction.getSorted().add(pf.getUuid());
                    HCF.getInstance().getBackend().createFaction(pf);
                    profile.setFaction(pf.getUuid());
                    profile.save();
                    p.sendMessage(Locale.COMMAND_FACTION_CREATED.toString());
                    p.sendMessage(Locale.COMMAND_FACTION_CREATED_HELP.toString());

                    FactionCreateEvent event = new FactionCreateEvent(pf, p);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    Bukkit.getServer().broadcastMessage(Placeholders.replacePlaceholders(Locale.COMMAND_FACTION_CREATED_BC.toString()
                            .replace("%faction%", pf.getName())
                            .replace("%player%", CC.translate(HCF.getInstance().getConfig().getString("Ranks." + AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName().toUpperCase()) + p.getName())), p, profile));

                    profile.setLastFactionEdit(System.currentTimeMillis());
                }
            }
        } else {
            p.sendMessage(Locale.COMMAND_FACTION_USAGE.toString().replace("%data%", "create <name>"));
        }
    }
}
