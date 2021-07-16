package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand {

    @Command(label = "ping")
    public void onPing(CommandData args) {
        Player player = args.getPlayer();

        if(args.getArgs().length >= 1) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if(target != null && target.isOnline())
                player.sendMessage(Locale.COMMAND_PING_OTHER.toString().replace("%player%", target.getName()).replace("%ping%", getPing(target) + ""));
            else
                player.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
        } else {
            player.sendMessage(Locale.COMMAND_PING_SELF.toString().replace("%ping%", getPing(player) + ""));
        }
    }

    public static int getPing(Player player) {
        String nmsVersion = HCF.getInstance().getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

        int ping = -1;
        try {
            Object nmsPlayer = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer").cast(player).getClass().getMethod("getHandle").invoke(player);
            ping = nmsPlayer.getClass().getField("ping").getInt(nmsPlayer);
        } catch(Exception e) {
            HCF.getInstance().getLogger().severe("Could not get ping of player!");
            e.printStackTrace();
        }
        return ping;
    }
}
