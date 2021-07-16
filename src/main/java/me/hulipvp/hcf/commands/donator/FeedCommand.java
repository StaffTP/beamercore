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

public class FeedCommand {

    public static Map<UUID, Long> feedCooldowns = new HashMap<>();

    @Command(label = "feed", permission = "command.feed", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        UUID uuid = player.getUniqueId();

        if (args.getArgs().length == 0) {
            if (player.hasPermission("hcf.staff")) {
                feed(player);
                player.sendMessage(C.color("&eYou were fed"));
            } else if (player.hasPermission("hcf.feed.nodelay")) {
                feed(player);
                player.sendMessage(C.color("&eYou were fed"));
            } else {
                long timestamp1 = feedCooldowns.getOrDefault(uuid, 0L);
                long millis1 = System.currentTimeMillis();
                long remaining1 = timestamp1 - millis1;

                if (remaining1 > 0L) {
                    player.sendMessage(ChatColor.RED + "You cannot do this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
                } else {
                    feedCooldowns.put(uuid, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(180L));
                    feed(player);
                    player.sendMessage(C.color("&eYou were fed"));
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

            feed(target);
            player.sendMessage(C.color("&eYou fed " + args.getArg(0)));
            target.sendMessage(C.color("&eYou were fed"));

        }
        return;
    }

        public void feed(Player player) {

            player.setFoodLevel(20);
            player.setSaturation(10);

        }
}
