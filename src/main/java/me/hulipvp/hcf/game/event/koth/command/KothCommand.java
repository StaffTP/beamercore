package me.hulipvp.hcf.game.event.koth.command;

import me.hulipvp.hcf.game.faction.type.event.KothFaction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.koth.Koth;
import me.hulipvp.hcf.game.event.koth.data.KothPoint;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.utils.CC;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.LocUtils;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.command.Completer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KothCommand {

    @Command(label = "koth", permission = "command.koth")
    public void onCommand(CommandData args) {
        CommandSender p = args.getSender();
        Koth koth;

        switch(args.length()) {
            case 1:
                if(Koth.getKoth(args.getArg(0)) != null) {
                    koth = Koth.getKoth(args.getArg(0));

                    p.sendMessage(C.color(koth.getName() + "&7:"));
                    p.sendMessage(C.color("  &eName: &f" + koth.getName()));
                    p.sendMessage(C.color("  &eCorner #1: &f" + ((koth.getPoint().getCorner1() == null) ? "null" : LocUtils.locationToFomattedString(koth.getPoint().getCorner1()))));
                    p.sendMessage(C.color("  &eCorner #2: &f" + ((koth.getPoint().getCorner2() == null) ? "null" : LocUtils.locationToFomattedString(koth.getPoint().getCorner2()))));
                    p.sendMessage(C.color("  &eTime: &f" + koth.getTime() + " minutes"));
                    p.sendMessage(C.color("  &eActive: &f" + ((Koth.getActiveKoth() == koth) ? "&aTrue" : "&cFalse")));
                    p.sendMessage(C.color("  &eSpecial: &f" + ((koth.isSpecial()) ? "&aTrue" : "&cFalse")));
                    p.sendMessage(C.color("  &ePearlable: &f" + ((koth.isPearlable()) ? "&aTrue" : "&cFalse")));
                } else {
                    p.sendMessage(Locale.COMMAND_KOTH_DOESNT_EXIST.toString().replace("%koth%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_KOTH_DOESNT_EXIST_SUBTEXT.toString().replace("%koth%", args.getArg(0)));
                }
                return;
            case 2:
                if(args.getArg(1).toLowerCase().equals("create")) {
                    if(Faction.getByName(args.getArg(0)) != null) {
                        p.sendMessage(Locale.COMMAND_KOTH_ALREADY_EXISTS.toString().replace("%koth%", args.getArg(0)));
                    } else {
                        koth = new Koth(args.getArg(0));
                        KothFaction faction = new KothFaction(null, koth.getName(), koth);
                        koth.setFaction(faction);
                        Koth.getKoths().put(koth.getName(), koth);
                        Faction.getFactions().put(faction.getUuid().toString(), faction);
                        HCF.getInstance().getBackend().createKoth(koth);
                        HCF.getInstance().getBackend().createFaction(faction);
                        p.sendMessage(Locale.COMMAND_KOTH_CREATED.toString().replace("%koth%", koth.getName()));
                    }
                    return;
                }

                if(Koth.getKoth(args.getArg(0)) == null) {
                    p.sendMessage(Locale.COMMAND_KOTH_DOESNT_EXIST.toString().replace("%koth%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_KOTH_DOESNT_EXIST_SUBTEXT.toString().replace("%koth%", args.getArg(0)));
                    return;
                }

                koth = Koth.getKoth(args.getArg(0));
                KothPoint point = koth.getPoint();

                switch(args.getArg(1).toLowerCase()) {
                    case "delete":
                        koth.stop();
                        KothFaction faction = koth.getFaction();
                        if(faction != null) {
                            faction.removeClaims();
                            Faction.getFactions().remove(faction.getUuid().toString());
                            HCF.getInstance().getBackend().deleteFaction(faction);
                        }

                        HCF.getInstance().getBackend().deleteKoth(koth);
                        Koth.getKoths().remove(koth.getName());
                        p.sendMessage(Locale.COMMAND_KOTH_DELETED.toString().replace("%koth%", koth.getName()));
                        return;
                    case "start":
                        koth.start();
                        return;
                    case "stop":
                        koth.stop();
                        return;
                    case "setpos1":
                        Player player = (Player) p;
                        point.setCorner1(player.getLocation());
                        point.reformCuboid();
                        koth.setPoint(point);
                        koth.save();
                        p.sendMessage(Locale.COMMAND_KOTH_SETPOS1.toString().replace("%koth%", koth.getName()));
                        return;
                    case "setpos2":
                        Player player2 = (Player) p;
                        point.setCorner2(player2.getLocation());
                        point.reformCuboid();
                        koth.setPoint(point);
                        koth.save();
                        p.sendMessage(Locale.COMMAND_KOTH_SETPOS2.toString().replace("%koth%", koth.getName()));
                        return;
                    case "special":
                        p.sendMessage(Locale.COMMAND_KOTH_SPECIAL_USAGE.toString());
                        return;
                    case "settime":
                        p.sendMessage(Locale.COMMAND_KOTH_SETTIME_USAGE.toString());
                        return;
                    case "setlocation":
                        Player player1 = (Player) p;
                        p.sendMessage(CC.translate("&eYou have set the location of &9" + koth.getName() + " &eto your location!"));
                        koth.getFaction().setHome(player1.getLocation());
                        koth.save();
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            case 3:
                if(Koth.getKoth(args.getArg(0)) == null) {
                    p.sendMessage(Locale.COMMAND_KOTH_DOESNT_EXIST.toString().replace("%koth%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_KOTH_DOESNT_EXIST_SUBTEXT.toString().replace("%koth%", args.getArg(0)));
                    return;
                }

                koth = Koth.getKoth(args.getArg(0));

                switch(args.getArg(1).toLowerCase()) {
                    case "special":
                        boolean bool = Boolean.valueOf(args.getArg(2));
                        koth.setSpecial(bool);
                        p.sendMessage(Locale.COMMAND_KOTH_SPECIAL_SET.toString().replace("%koth%", koth.getName()).replace("%status%", String.valueOf(koth.isSpecial())));
                        koth.save();
                        return;
                    case "pearlable":
                        boolean pearl = Boolean.valueOf(args.getArg(2));
                        koth.setPearlable(pearl);
                        koth.save();
                        p.sendMessage(Locale.COMMAND_KOTH_PEARLABLE_SET.toString().replace("%koth%", koth.getName()).replace("%status%", String.valueOf(koth.isPearlable())));
                        return;
                    case "settime":
                        Integer time;

                        try {
                            time = Integer.parseInt(args.getArg(2));
                        } catch(NumberFormatException e) {
                            p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", args.getArg(2)));
                            return;
                        }

                        koth.setTime(time);
                        koth.save();
                        p.sendMessage(Locale.COMMAND_KOTH_SETTIME_SET.toString().replace("%koth%", koth.getName()));
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            default:
                this.sendUsage(p);
        }
    }

    @Completer(label = "koth")
    public List<String> onComplete(CommandData args) {
        List<String> list = new ArrayList<>();
        if(args.length() == 1)
            list.addAll(Koth.getKoths().keySet());
        if(args.length() == 2)
            list.addAll(Arrays.asList("create", "delete", "start", "stop", "setpos1", "setpos2", "settime", "pearlable", "special"));
        return list;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Locale.COMMAND_KOTH_USAGE.toString());
    }
}
