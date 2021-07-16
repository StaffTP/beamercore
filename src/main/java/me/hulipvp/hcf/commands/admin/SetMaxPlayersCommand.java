package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class SetMaxPlayersCommand {

    @Command(label = "setmaxplayers", aliases = {"smp"}, permission = "command.setmaxplayers", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = args.getPlayer();
        if(args.length() != 1) {
            player.sendMessage(C.color("&cUsage: /smp <amount>"));
            return;
        }

        if(!StringUtils.isInt(args.getArg(0))) {
            player.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(0)));
            return;
        }

        int amount = Integer.parseInt(args.getArg(0));
        try {
            setMaxPlayers(amount);
        } catch(ReflectiveOperationException e) {
            player.sendMessage(C.color("&cError while attempting to set max players."));
            return;
        }

        player.sendMessage(C.color("&aSet max players to " + amount + "."));
    }

    private void setMaxPlayers(int max)
            throws ReflectiveOperationException {
        String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);

        Object playerList = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".CraftServer").getDeclaredMethod("getHandle", (Class<?>[]) null).invoke(Bukkit.getServer(), (Object[]) null);
        Field maxPlayers = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");

        maxPlayers.setAccessible(true);
        maxPlayers.set(playerList, max);
    }
}
