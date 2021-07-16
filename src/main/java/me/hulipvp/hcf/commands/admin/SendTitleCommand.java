package me.hulipvp.hcf.commands.admin;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * @author scynse (scynse@fusiongames.dev)
 * 7/14/2021 / 10:07 AM
 * GlacialHCF / me.hulipvp.hcf.commands.admin
 */
public class SendTitleCommand {

    @Command(label = "sendtitle", permission = "command.sendtitle")
    public void onSendTitle(CommandData args) {
        LunarClientAPI api = LunarClientAPI.getInstance();
        if (args.length() < 3) {
            args.getSender().sendMessage(CC.translate("&CUsage: /sendtitle <player;all> <seconds> <message>"));
            return;
        }

        if (args.getArg(0).equalsIgnoreCase("all")) {
            int amount;
            try {
                amount = Integer.parseInt(args.getArg(1));
            } catch (NumberFormatException e) {
                args.getSender().sendMessage(CC.translate("&cInvalid Number!"));
                return;
            }
            StringBuilder message3 = new StringBuilder();
            for(int i = 2; i < args.length(); i++) {
                message3.append(args.getArg(i)).append(" ");
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                api.sendTitle(p, TitleType.TITLE, CC.translate(message3.toString()), Duration.ofSeconds(amount));
            }
            args.getSender().sendMessage(CC.translate("&eYou sent a title to &dall &eplayers!"));

        } else {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                args.getSender().sendMessage(CC.translate("&cNot a player!"));
                return;
            }
            int amount;
            try {
                 amount = Integer.parseInt(args.getArg(1));
            } catch (NumberFormatException e) {
                args.getSender().sendMessage(CC.translate("&cInvalid Number!"));
                return;
            }
            StringBuilder message = new StringBuilder();
            for(int i = 2; i < args.length(); i++) {
                message.append(args.getArg(i)).append(" ");
            }
            api.sendTitle(target, TitleType.TITLE, CC.translate(message.toString()), Duration.ofSeconds(amount));
            args.getSender().sendMessage(CC.translate("&eYou sent a title to &d" + target.getName() + "&e!"));

        }


    }


}
