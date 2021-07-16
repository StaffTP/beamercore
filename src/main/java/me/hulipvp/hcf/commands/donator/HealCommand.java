package me.hulipvp.hcf.commands.donator;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HealCommand {

    public static Map<UUID, Long> healCooldowns = new HashMap<>();

    @Command(label = "heal", permission = "command.heal", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        UUID uuid = player.getUniqueId();

        if (args.getArgs().length == 0) {
            if (player.hasPermission("hcf.staff")) {
                heal(player);
                player.sendMessage(C.color("&eYou were healed"));
            } else if (player.hasPermission("hcf.heal.nodelay")) {
                heal(player);
                player.sendMessage(C.color("&eYou were healed"));
            } else {
                long timestamp1 = healCooldowns.getOrDefault(uuid, 0L);
                long millis1 = System.currentTimeMillis();
                long remaining1 = timestamp1 - millis1;

                if (remaining1 > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot do this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
                } else {
                    healCooldowns.put(uuid, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(180L));
                    heal(player);
                    player.sendMessage(C.color("&eYou were healed"));
                }
            }
        } else {
            if (!player.hasPermission("hcf.staff")) {
                player.sendMessage(Locale.NO_PERMISSION.toString());
                return;
            }

            Player target = Bukkit.getPlayer(args.getArg(0));

            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                return;
            }

            heal(target);
            player.sendMessage(C.color("&eYou healed " + args.getArg(0)));
            target.sendMessage(C.color("&eYou were healed"));

        }
        return;
    }

        public void heal(Player player) {

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(10);
            player.setFireTicks(0);

        }
}
