package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.system.SafezoneFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnCommand {

    @Command(label = "spawn", permission = "hcf.command.spawn", playerOnly = true)
    public void onSpawn(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByUuid(p.getUniqueId());
        Faction faction = Faction.getByName("Spawn");
        ServerTimer sotw = ServerTimer.getTimer(ServerTimerType.SOTW);

        if (p.hasPermission("hcf.command.spawn.bypass")) {
            if (faction != null && faction.getHome() != null)
                p.teleport(faction.getHome());
            return;
        }

        if (sotw != null) {
            if (sotw.getTimeRemaining() > 0) {
                if (faction != null && faction.getHome() != null)
                    p.teleport(faction.getHome());
                p.sendMessage(CC.translate("&aYou have been teleported to spawn!"));
            } else {
                p.sendMessage(CC.translate("&cYou can only run this command while sotw is active!"));
            }
        }


    }

    @Command(label = "setspawn", permission = "command.setspawn", playerOnly = true)
    public void onSetSpawn(CommandData args) {
        Player player = (Player) args.getSender();
        Faction spawn = Faction.getByName("Spawn");
        if(!(spawn instanceof SafezoneFaction)) {
            player.sendMessage(ChatColor.RED + "Please type /instatehcf to create default factions and then set the spawn.");
            return;
        }

        if(!spawn.isInsideClaim(player.getLocation())) {
            player.sendMessage(C.color("&ePlease stand inside of the " + ((SafezoneFaction) spawn).getColoredName() + " &efaction to set the spawn point."));
            return;
        }

        spawn.setHome(player.getLocation());
        spawn.save();

        player.sendMessage(ChatColor.GREEN + "Spawn point successfully set.");
    }
}
