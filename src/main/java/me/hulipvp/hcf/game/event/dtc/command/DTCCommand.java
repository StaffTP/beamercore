package me.hulipvp.hcf.game.event.dtc.command;

import me.hulipvp.hcf.game.faction.type.event.DTCFaction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.command.Completer;
import me.hulipvp.hcf.game.event.dtc.DTC;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DTCCommand {

    @Command(label = "dtc", permission = "command.dtc", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        DTC dtc;

        switch(args.length()) {
            case 1:
                if(DTC.getDTC(args.getArg(0)) != null) {
                    dtc = DTC.getDTC(args.getArg(0));

                    p.sendMessage(C.color(dtc.getName() + "&7:"));
                    p.sendMessage(C.color("  &eName: &f" + dtc.getName()));
                    p.sendMessage(C.color("  &eActive: &f" + ((DTC.getActiveDTC() == dtc) ? "&aTrue" : "&cFalse")));
                } else {
                    p.sendMessage(Locale.COMMAND_DTC_DOESNT_EXIST.toString().replace("%dtc%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_DTC_DOESNT_EXIST_SUBTEXT.toString().replace("%dtc%", args.getArg(0)));
                }
                return;
            case 2:
                if(args.getArg(1).toLowerCase().equals("create")) {
                    if(Faction.getByName(args.getArg(0)) != null) {
                        p.sendMessage(Locale.COMMAND_DTC_ALREADY_EXISTS.toString().replace("%dtc%", args.getArg(0)));
                    } else {
                        dtc = new DTC(args.getArg(0));
                        DTCFaction faction = new DTCFaction(null, dtc.getName(), dtc);
                        dtc.setFaction(faction);
                        DTC.getDTCs().put(dtc.getName(), dtc);
                        Faction.getFactions().put(faction.getUuid().toString(), faction);
                        HCF.getInstance().getBackend().createDTC(dtc);
                        HCF.getInstance().getBackend().createFaction(faction);
                        p.sendMessage(Locale.COMMAND_DTC_CREATED.toString().replace("%dtc%", dtc.getName()));
                    }
                    return;
                }

                if(DTC.getDTC(args.getArg(0)) == null) {
                    p.sendMessage(Locale.COMMAND_DTC_DOESNT_EXIST.toString().replace("%dtc%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_DTC_DOESNT_EXIST_SUBTEXT.toString().replace("%dtc%", args.getArg(0)));
                    return;
                }

                dtc = DTC.getDTC(args.getArg(0));

                switch(args.getArg(1).toLowerCase()) {
                    case "delete":
                        dtc.stop();
                        DTCFaction faction = dtc.getFaction();
                        if(faction != null) {
                            faction.removeClaims();
                            HCF.getInstance().getBackend().deleteFaction(dtc.getFaction());
                            Faction.getFactions().remove(faction.getUuid().toString());
                        }

                        HCF.getInstance().getBackend().deleteDTC(dtc);
                        DTC.getDTCs().remove(dtc.getName());
                        p.sendMessage(Locale.COMMAND_DTC_DELETED.toString().replace("%dtc%", dtc.getName()));
                        return;
                    case "start":
                        dtc.start();
                        return;
                    case "stop":
                        dtc.stop();
                        return;
                    case "setpoints":
                        p.sendMessage(Locale.COMMAND_DTC_SETPOINTS_USAGE.toString());
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            case 3:
                switch(args.getArg(1).toLowerCase()) {
                    case "setpoints":
                        p.sendMessage(Locale.COMMAND_DTC_SETPOINTS_USAGE.toString());
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            case 4:
                if(DTC.getDTC(args.getArg(0)) == null) {
                    p.sendMessage(Locale.COMMAND_DTC_DOESNT_EXIST.toString().replace("%dtc%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_DTC_DOESNT_EXIST_SUBTEXT.toString().replace("%dtc%", args.getArg(0)));
                    return;
                }

                dtc = DTC.getDTC(args.getArg(0));

                switch(args.getArg(1).toLowerCase()) {
                    case "setpoints":
                        String rawFaction = args.getArg(2);
                        String rawPoints = args.getArg(3);

                        Faction fac = Faction.getByName(rawFaction);

                        if(fac != null) {
                            if(fac instanceof PlayerFaction) {
                                PlayerFaction pf = (PlayerFaction) fac;

                                try {
                                    int points = Integer.parseInt(rawPoints);

                                    dtc.getPoints().put(pf.getUuid().toString(), points);
                                } catch(NumberFormatException e) {
                                    p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawPoints));
                                }
                            } else {
                                p.sendMessage(Locale.COMMAND_FACTION_NOT_PLAYER.toString());
                            }
                        } else {
                            p.sendMessage(Locale.COMMAND_FACTION_DOESNT_EXIST.toString().replace("%faction%", args.getArg(1)));
                        }
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            default:
                this.sendUsage(p);
        }
    }

    @Completer(label = "dtc")
    public List<String> onComplete(CommandData args) {
        List<String> list = new ArrayList<>();
        if(args.length() == 1)
            list.addAll(DTC.getDTCs().keySet());
        if(args.length() == 2)
            list.addAll(Arrays.asList("create", "delete", "start", "stop"));
        return list;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Locale.COMMAND_DTC_USAGE.toString());
    }
}
