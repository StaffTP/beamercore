package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimer;
import me.hulipvp.hcf.game.timer.type.player.PlayerTimerType;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.TimeUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvpCommand {

    @Command(label = "pvp", playerOnly = true)
    public void onCommand(CommandData args) {
        this.sendHelp(args.getPlayer());
    }

    @Command(label = "pvp.enable", playerOnly = true)
    public void onEnable(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        PlayerTimer timer = profile.getTimerByType(PlayerTimerType.PVPTIMER);
        if(timer == null && ((timer = profile.getTimerByType(PlayerTimerType.STARTING)) == null)) {
            p.sendMessage(Locale.COMMAND_PVP_NO_TIMER.toString());
            return;
        }

        profile.removeTimersByType(timer.getType());
        p.sendMessage(Locale.COMMAND_PVP_ENABLED.toString());
    }

    @Command(label = "pvp.give", permission = "command.pvp", playerOnly = true)
    public void onGive(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() != 2) {
            this.sendHelp(p);
        } else {
            Player target = Bukkit.getPlayer(args.getArg(1));
            if(target == null) {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            HCFProfile profile = HCFProfile.getByPlayer(target);
            if(profile.getTimerByType(PlayerTimerType.PVPTIMER) != null) {
                p.sendMessage(Locale.COMMAND_PVP_ALREADY_HAS.toString());
                return;
            }

            PlayerTimer timer = new PlayerTimer(target, PlayerTimerType.PVPTIMER);
            timer.setPaused(true);
            timer.add();
            p.sendMessage(Locale.COMMAND_PVP_GIVEN.toString().replace("%player%", target.getName()));
        }
    }

    @Command(label = "pvp.remove", permission = "command.pvp", playerOnly = true)
    public void onRemoveArgument(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() != 2) {
            this.sendHelp(p);
        } else {
            Player target = Bukkit.getPlayer(args.getArg(1));
            if(target == null) {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            HCFProfile profile = HCFProfile.getByPlayer(target);
            PlayerTimer timer = profile.getTimerByType(PlayerTimerType.PVPTIMER);
            if(timer == null) {
                p.sendMessage(Locale.COMMAND_PVP_NO_TIMER.toString());
                return;
            }

            timer.cancel();
            profile.removeTimersByType(PlayerTimerType.PVPTIMER);
            p.sendMessage(Locale.COMMAND_PVP_REMOVED.toString().replace("%player%", target.getName()));
        }
    }

    @Command(label = "pvp.set", permission = "command.pvp", playerOnly = true)
    public void onSetArgument(CommandData args) {
        Player p = (Player) args.getSender();

        if(args.length() != 3) {
            this.sendHelp(p);
        } else {
            Player target = Bukkit.getPlayer(args.getArg(1));
            String rawInt = args.getArg(2);
            if(target == null) {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            }

            long duration;
            try {
                duration = System.currentTimeMillis() - TimeUtils.timeFromString(rawInt, false);
            } catch(Exception e) {
                p.sendMessage(Locale.INVALID_TIME.toString()
                    .replace("%number%", rawInt)
                );
                return;
            }

            HCFProfile profile = HCFProfile.getByPlayer(target);
            PlayerTimer timer = profile.getTimerByType(PlayerTimerType.PVPTIMER);
            if(timer == null) {
                timer = new PlayerTimer(target, PlayerTimerType.PVPTIMER);
                timer.setPaused(true);
                timer.add();
            }

            timer.setLength(duration);
            p.sendMessage(Locale.COMMAND_PVP_SET.toString()
                    .replace("%player%", target.getName())
                    .replace("%time%", TimeUtils.getFormatted(timer.getTimeRemaining(), true, true))
            );
        }
    }

    @Command(label = "pvp.revive", permission = "command.pvp.revive", playerOnly = true)
    public void onReviveArgument(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(args.length() != 2) {
            this.sendHelp(p);
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
            String targetName;
            UUID targetUuid;

            if(target == null) {
                p.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                return;
            } else {
                targetName = target.getName();
                targetUuid = target.getUniqueId();
            }

            HCFProfile targetProfile = HCFProfile.getByUuid(targetUuid);
            if(profile.getLives() > 0) {
                if (targetProfile.getBannedTill() != 0) {
                    if (System.currentTimeMillis() < targetProfile.getBannedTill()) {
                        profile.setLives(profile.getLives() - 1);
                        targetProfile.setBannedTill(0);
                        targetProfile.save();
                        p.sendMessage(Locale.COMMAND_REVIVE_SUCCESS.toString().replace("%player%", targetName));
                        return;
                    }
                }
            } else {
                p.sendMessage(Locale.COMMAND_REVIVE_NO_LIVES.toString());
                return;
            }

            p.sendMessage(Locale.COMMAND_REVIVE_NO_DEATHBAN.toString().replace("%player%", targetName));
        }
    }

    @Command(label = "pvp.test", permission = "command.pvp.test", playerOnly = true)
    public void onTest(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getTimerByType(PlayerTimerType.COMBAT) != null) {
            profile.removeTimersByType(PlayerTimerType.COMBAT);
        } else {
            PlayerTimer timer = new PlayerTimer(p, PlayerTimerType.COMBAT);
            timer.add();
        }
    }

    private void sendHelp(Player player) {
        for(String str : HCF.getInstance().getMessagesFile().getPvpHelp())
            player.sendMessage(C.color(str
                    .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                    .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                    .replace("%servername%", ConfigValues.SERVER_NAME)
                    .replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase())
                    .replace("%website%", ConfigValues.SERVER_WEBSITE)
                    .replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK)
                    .replace("%store%", ConfigValues.SERVER_STORE))
            );
    }
}
