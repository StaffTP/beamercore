package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class DebugHCFCommand {

    @Command(label = "debughcf")
    public void onCommand(CommandData args) {
        CommandSender sender = args.getSender();

        if (sender instanceof ConsoleCommandSender
                || (sender.hasPermission("hcf.command.debug") || sender.getName().equalsIgnoreCase("nickmc") || sender.getName().equalsIgnoreCase("jradmin"))) {

            sender.sendMessage(C.color(ConfigValues.SERVER_PRIMARY + "Debug Information:"));
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Online Players: &f") + Bukkit.getOnlinePlayers().size());
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " TPS: &f") + Bukkit.spigot().getTPS()[0]);
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Backend Driver: &f") + HCF.getInstance().getBackend().getType().getVerboseName());
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Profiles: &f") + HCFProfile.getProfiles().size());
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Factions: &f") + Faction.getFactions().size());
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Koths: &f") + Koth.getKoths().size());
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Conquests: &f") + Conquest.getConquests().size());
            sender.sendMessage(C.color("  &7-" + ConfigValues.SERVER_SECONDARY + " Mountains: &f") + Mountain.getMountains().size());
        }
    }
}
