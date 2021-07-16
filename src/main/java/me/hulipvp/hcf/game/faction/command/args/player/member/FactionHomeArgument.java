package me.hulipvp.hcf.game.faction.command.args.player.member;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class FactionHomeArgument {

    @Command(label = "f.home", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);
        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
        } else {
            PlayerFaction pf = profile.getFactionObj();
            if(profile.hasTimer(PlayerTimerType.COMBAT)) {
                p.sendMessage(Locale.COMMAND_FACTION_HOME_SPAWN_TIMER.toString());
                return;
            }
            if(profile.hasTimer(PlayerTimerType.PVPTIMER)) {
                p.sendMessage(Locale.COMMAND_FACTION_HOME_PVP_TIMER.toString());
                return;
            }
            if (p.getWorld().getEnvironment().equals(World.Environment.THE_END) && ConfigValues.FACTIONS_HOME_DISABLE_IN_END) {
                p.sendMessage(Locale.COMMAND_FACTION_HOME_UNABLE.toString());
                return;
            }

            if(pf.getHome() == null) {
                p.sendMessage(Locale.COMMAND_FACTION_HOME_NOT_SET.toString());
            } else {
                List<Player> nearbyPlayers = getNearbyPlayers(p, ConfigValues.FACTIONS_INSTANT_HOME_RADIUS);
                if(Faction.getByLocation(p.getLocation()) instanceof SafezoneFaction || nearbyPlayers.isEmpty()) {
                    p.teleport(pf.getHome());
                    p.sendMessage(Locale.FACTION_WARPING.toString().replace("%faction%", pf.getName()));
                } else {
                    PlayerTimer timer = new PlayerTimer(p, PlayerTimerType.HOME);
                    timer.add();
                    p.sendMessage(Locale.COMMAND_FACTION_HOME_WAITING.toString());
                }
            }
        }
    }

    private List<Player> getNearbyPlayers(Player player, int radius) {
        return player.getNearbyEntities(radius, radius, radius)
                .stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
    }
}
