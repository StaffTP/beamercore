package me.hulipvp.hcf.commands.admin;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.timer.Timer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;

public class EotwCommand {

    @Command(label = "eotw", permission = "command.eotw")
    public void onCommand(CommandData args) {
        args.getSender().sendMessage(Locale.COMMAND_EOTW_USAGE.toString());
    }

    @Command(label = "eotw.start", permission = "command.eotw.start")
    public void onStart(CommandData args) {
        boolean foundTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .anyMatch(timer -> timer.getType() == ServerTimerType.EOTW);

        if(foundTimer) {
            args.getSender().sendMessage(Locale.COMMAND_EOTW_ALREADY_STARTING.toString());
            return;
        }

        ServerTimer timer = new ServerTimer(ServerTimerType.EOTW);
        timer.add();

        Bukkit.broadcastMessage(Locale.COMMAND_EOTW_STARTING.toString());
    }

    @Command(label = "eotw.stop", permission = "command.eotw.stop")
    public void onStop(CommandData args) {
        ServerTimer eotwTimer = Timer.getTimers().values().stream()
                .filter(ServerTimer.class::isInstance)
                .map(ServerTimer.class::cast)
                .filter(timer -> timer.getType() == ServerTimerType.EOTW)
                .findFirst()
                .orElse(null);

        if(eotwTimer == null) {
            args.getSender().sendMessage(Locale.COMMAND_EOTW_NOT_RUNNING.toString());
            return;
        }

        Timer.getTimers().remove(eotwTimer.getUuid());
        Bukkit.broadcastMessage(Locale.COMMAND_EOTW_STOPPED.toString());
    }

    @Command(label = "eotw.commence", permission = "command.eotw.commence")
    public void onCommence(CommandData args) {
        if(args.getSender() instanceof Player) {
            args.getSender().sendMessage(Locale.COMMAND_EOTW_CONSOLE_ONLY.toString());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
            Faction.getFactions().values().parallelStream()
                    .filter(PlayerFaction.class::isInstance)
                    .map(PlayerFaction.class::cast)
                    .forEach(faction -> {
                        faction.setDtr(-1);
                        if (faction.getRegenTask() != null) faction.getRegenTask().cancel();
                        faction.save();
                    });
        });

        Koth eotw = Koth.getKoth("EOTW");
        if(eotw != null)
            eotw.start();

        Bukkit.broadcastMessage(Locale.COMMAND_EOTW_COMMENCED.toString());
        LunarClientAPI api = LunarClientAPI.getInstance();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (api.isRunningLunarClient(player)) {
                api.sendTitle(player, TitleType.TITLE, CC.translate("&cEOTW has commenced!"), Duration.ofSeconds(3));
            }
        }
    }
}
