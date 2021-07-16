package me.hulipvp.hcf.commands.admin;

import com.google.common.base.Joiner;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class NotesCommand {

    @Command(label = "notes", permission = "command.notes")
    public void onCommand(CommandData args) {
        switch(args.length()) {
            case 1: {
                HCFProfile profile = getProfile(args.getSender(), args.getArg(0));
                if(profile != null) {
                    args.getSender().sendMessage(Locale.COMMAND_NOTES_PLAYER.toString().replace("%player%", profile.getName()));
                    profile.getNotes().removeIf(note -> !note.contains(";"));

                    if(profile.getNotes().isEmpty()) {
                        args.getSender().sendMessage(Locale.COMMAND_NOTES_LIST_NONE.toString());
                    } else {
                        for(int i = 0; i < profile.getNotes().size(); i++) {
                            String[] split = profile.getNotes().get(i).split(";");
                            String staffMember = split[0];
                            String message = split[1];

                            args.getSender().sendMessage(Locale.COMMAND_NOTES_LIST.toString().replace("%index%", (i + 1) + "").replace("%staffMember%", staffMember).replace("%note%", message));
                        }
                    }
                }
                break;
            }
            default: {
                if(args.length() >= 2) {
                    switch(args.getArg(0)) {
                        case "add": {
                            HCFProfile profile = getProfile(args.getSender(), args.getArg(1));
                            if(profile != null) {
                                String staffMember = args.getSender().getName();
                                if(args.length() > 2) {
                                    String message = Joiner.on(' ').join(args.getArgs()).replaceFirst("add ", "").replaceFirst(args.getArg(1), "").replaceFirst(" ", "");

                                    profile.getNotes().add(staffMember + ";" + message);
                                    profile.save();
                                    args.getSender().sendMessage(C.color("&eAdded note '" + message + "&e'."));
                                } else {
                                    args.getSender().sendMessage(C.color("&cUsage: /notes add <player> <message>"));
                                }
                            }
                            break;
                        }
                        case "remove": {
                            HCFProfile profile = getProfile(args.getSender(), args.getArg(1));
                            if(profile != null) {
                                int index;
                                try {
                                    index = Integer.valueOf(args.getArg(2)) - 1;
                                } catch (Exception e) {
                                    args.getSender().sendMessage(C.color("&cInvalid index."));
                                    return;
                                }

                                if(args.length() < 2 || !StringUtils.isAlphanumeric(args.getArg(2))) {
                                    args.getSender().sendMessage(C.color("&cInvalid index."));
                                    return;
                                }

                                if(profile.getNotes().isEmpty() || index < 0 || index > profile.getNotes().size() - 1) {
                                    args.getSender().sendMessage(C.color("&cInvalid index."));
                                    return;
                                }

                                String note = profile.getNotes().get(index);
                                String message = note.split(";")[1];

                                profile.getNotes().remove(index);
                                profile.save();
                                args.getSender().sendMessage(C.color("&eRemoved note '" +  message + "&e'."));
                            }
                            break;
                        }
                        default:
                            args.getSender().sendMessage(Locale.COMMAND_NOTES_USAGE.toString());
                    }
                } else {
                    args.getSender().sendMessage(Locale.COMMAND_NOTES_USAGE.toString());
                }
                break;
            }
        }
    }

    private HCFProfile getProfile(CommandSender sender, String target) {
        String targetName;
        UUID targetUuid;

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        if(targetPlayer == null) {
            sender.sendMessage(Locale.TARGET_NOT_FOUND.toString().replace("%name%", target));
            return null;
        } else {
            targetName = targetPlayer.getName();
            targetUuid = targetPlayer.getUniqueId();
        }

        HCFProfile targetProfile = HCFProfile.getByUuid(targetUuid);
        if(targetProfile.getName() != null)
            targetProfile.setName(targetName);

        return targetProfile;
    }
}
