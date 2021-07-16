package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.utils.*;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.UUID;

public class LivesCommand {

    @Command(label = "lives", playerOnly = true)
    public void onCommand(CommandData args) {
        if (ServerTimer.isEotw()) {
            args.getSender().sendMessage(Locale.COMMAND_LIVES_EOTW.toString());
            return;
        }

        Player p = (Player) args.getSender();

        if (args.length() != 1) {
            HCFProfile profile = HCFProfile.getByPlayer(p);
            p.sendMessage(Locale.COMMAND_LIVES.toString()
                    .replace("%lives%", String.valueOf(profile.getLives()))
                    .replace("%store%", ConfigValues.SERVER_STORE)
            );
        } else {
            TaskUtils.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(0));
                String targetName;
                UUID targetUuid;

                if (target == null || !target.hasPlayedBefore()) {
                    p.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(0)));
                    return;
                } else {
                    targetName = target.getName();
                    targetUuid = target.getUniqueId();
                }

                HCFProfile targetProfile = HCFProfile.getByUuid(targetUuid);
                p.sendMessage(Locale.COMMAND_LIVES_OTHER.toString()
                        .replace("%name%", targetName)
                        .replace("%lives%", String.valueOf(targetProfile.getLives()))
                );
            });
        }
    }

    @Command(label = "lives.set", permission = "command.lives")
    public void onLivesSet(CommandData args) {
        CommandSender sender = args.getSender();
        if (ServerTimer.isEotw()) {
            sender.sendMessage(Locale.COMMAND_LIVES_EOTW.toString());
            return;
        }

        if (args.length() < 3) {
            this.sendUsage(sender);
        } else {
            TaskUtils.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                String targetName;
                UUID targetUuid;
                HCFProfile profile;

                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    return;
                } else {
                    targetName = target.getName();
                    targetUuid = target.getUniqueId();
                }

                profile = HCFProfile.getByUuid(targetUuid);
                String rawInt = args.getArg(2);
                if (!StringUtils.isInt(rawInt)) {
                    sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                    return;
                }

                int amount = Integer.parseInt(rawInt);
                profile.setLives(amount);
                profile.save();
                sender.sendMessage(Locale.COMMAND_LIVES_SET.toString()
                        .replace("%name%", targetName)
                        .replace("%amount%", String.valueOf(amount))
                );
            });
        }
    }

    @Command(label = "lives.give", playerOnly = true)
    public void onLivesGive(CommandData args) {
        CommandSender sender = args.getSender();
        if (ServerTimer.isEotw()) {
            sender.sendMessage(Locale.COMMAND_LIVES_EOTW.toString());
            return;
        }

        if (args.length() < 3) {
            this.sendUsage(sender);
        } else {
            TaskUtils.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                String targetName;
                UUID targetUuid;
                HCFProfile targetProfile;

                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    return;
                } else {
                    targetName = target.getName();
                    targetUuid = target.getUniqueId();
                }

                targetProfile = HCFProfile.getByUuid(targetUuid);
                String rawInt = args.getArg(2);
                if (!StringUtils.isInt(rawInt)) {
                    sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                    return;
                }

                int amount = Integer.parseInt(rawInt);
                HCFProfile profile = HCFProfile.getByPlayer(args.getPlayer());
                if (amount < 0 || profile.getLives() < amount) {
                    sender.sendMessage(Locale.COMMAND_LIVES_NOT_ENOUGH.toString());
                    return;
                }

                profile.setLives(profile.getLives() - amount);
                profile.save();

                targetProfile.setLives(targetProfile.getLives() + amount);
                targetProfile.save();
                sender.sendMessage(Locale.COMMAND_LIVES_GIVEN.toString()
                        .replace("%name%", targetName)
                        .replace("%amount%", String.valueOf(amount))
                );

                if (target != null && target.isOnline()) {
                    Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                    targetPlayer.sendMessage(Locale.COMMAND_LIVES_RECEIVED.toString()
                            .replace("%name%", targetName)
                            .replace("%amount%", String.valueOf(amount))
                    );
                }
            });
        }
    }

    @Command(label = "lives.add", permission = "command.lives")
    public void onLivesAdd(CommandData args) {
        CommandSender sender = args.getSender();
        if (ServerTimer.isEotw()) {
            sender.sendMessage(Locale.COMMAND_LIVES_EOTW.toString());
            return;
        }

        if (args.length() < 3) {
            this.sendUsage(sender);
        } else {
            TaskUtils.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                String targetName;
                UUID targetUuid;
                HCFProfile profile;

                if (target == null) {
                    sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    return;
                } else {
                    targetName = target.getName();
                    targetUuid = target.getUniqueId();
                }

                profile = HCFProfile.getByUuid(targetUuid);
                String rawInt = args.getArg(2);
                if (!StringUtils.isInt(rawInt)) {
                    sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                    return;
                }

                int amount = Integer.parseInt(rawInt);
                profile.setLives(profile.getLives() + amount);
                profile.save();
                sender.sendMessage(Locale.COMMAND_LIVES_GIVEN.toString()
                        .replace("%name%", targetName)
                        .replace("%amount%", String.valueOf(amount))
                );
            });
        }
    }

    @Command(label = "lives.take", permission = "command.lives")
    public void onLivesTake(CommandData args) {
        CommandSender sender = args.getSender();
        if (ServerTimer.isEotw()) {
            sender.sendMessage(Locale.COMMAND_LIVES_EOTW.toString());
            return;
        }

        if (args.length() < 3) {
            this.sendUsage(sender);
        } else {
            TaskUtils.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                String targetName;
                UUID targetUuid;
                HCFProfile profile;

                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    return;
                } else {
                    targetName = target.getName();
                    targetUuid = target.getUniqueId();
                }

                profile = HCFProfile.getByUuid(targetUuid);
                String rawInt = args.getArg(2);
                if (!StringUtils.isInt(rawInt)) {
                    sender.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawInt));
                    return;
                }

                int amount = Integer.parseInt(rawInt);
                profile.setLives(profile.getLives() - amount);
                profile.save();
                sender.sendMessage(Locale.COMMAND_LIVES_TAKEN.toString()
                        .replace("%name%", targetName)
                        .replace("%amount%", String.valueOf(amount))
                );
            });
        }
    }

    @Command(label = "lives.checkdeathban", permission = "command.lives")
    public void onCheckDeathban(CommandData args) {
        CommandSender sender = args.getSender();
        if (ServerTimer.isEotw()) {
            sender.sendMessage(Locale.COMMAND_LIVES_EOTW.toString());
            return;
        }

        if (args.length() < 2) {
            this.sendUsage(sender);
        } else {
            TaskUtils.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(1));
                String targetName;
                UUID targetUuid;
                HCFProfile profile;

                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", args.getArg(1)));
                    return;
                } else {
                    targetName = target.getName();
                    targetUuid = target.getUniqueId();
                }

                profile = HCFProfile.getByUuid(targetUuid);
                if (profile.getBannedTill() == 0 || (System.currentTimeMillis() > profile.getBannedTill())) {
                    sender.sendMessage(Locale.COMMAND_REVIVE_NO_DEATHBAN.toString().replace("%player%", targetName));
                    return;
                }

                String remainingTime = TimeUtils.getTimeTill(new Timestamp(profile.getBannedTill()));
                if (remainingTime != null)
                    sender.sendMessage(Locale.COMMAND_LIVES_CHECK.toString().replace("%name%", targetName).replace("%time%", remainingTime));
                else
                    sender.sendMessage(Locale.COMMAND_REVIVE_NO_DEATHBAN.toString().replace("%player%", targetName));
            });
        }
    }

    private void sendUsage(CommandSender sender) {
        for (String str : HCF.getInstance().getMessagesFile().getLivesHelp())
            sender.sendMessage(C.color(str
                    .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                    .replace("%secondary%", ConfigValues.SERVER_SECONDARY))
            );
    }
}
