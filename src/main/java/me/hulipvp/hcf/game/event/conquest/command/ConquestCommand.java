package me.hulipvp.hcf.game.event.conquest.command;

import me.hulipvp.hcf.game.faction.type.event.ConquestFaction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import me.hulipvp.hcf.utils.command.Completer;
import me.hulipvp.hcf.game.event.conquest.Conquest;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZone;
import me.hulipvp.hcf.game.event.conquest.data.ConquestZoneType;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.utils.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConquestCommand {

    @Command(label = "conquest", permission = "command.conquest", playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();

        Conquest conquest;

        switch(args.length()) {
            case 1:
                if(Conquest.getConquest(args.getArg(0)) != null) {
                    conquest = Conquest.getConquest(args.getArg(0));

                    p.sendMessage(C.color(conquest.getName() + "&7:"));
                    p.sendMessage(C.color("  &eName: &f" + conquest.getName()));
                    p.sendMessage(C.color("  &eActive: &f" + ((Conquest.getActiveConquest() == conquest) ? "&aTrue" : "&cFalse")));
                } else {
                    p.sendMessage(Locale.COMMAND_CONQUEST_DOESNT_EXIST.toString().replace("%conquest%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_CONQUEST_DOESNT_EXIST_SUBTEXT.toString().replace("%conquest%", args.getArg(0)));
                }
                return;
            case 2:
                if(args.getArg(1).toLowerCase().equals("create")) {
                    if(Faction.getByName(args.getArg(0)) != null) {
                        p.sendMessage(Locale.COMMAND_CONQUEST_ALREADY_EXISTS.toString().replace("%conquest%", args.getArg(0)));
                    } else {
                        conquest = new Conquest(args.getArg(0));
                        ConquestFaction faction = new ConquestFaction(null, conquest.getName(), conquest);
                        conquest.setFaction(faction);
                        Conquest.getConquests().put(conquest.getName(), conquest);
                        Faction.getFactions().put(faction.getUuid().toString(), faction);
                        HCF.getInstance().getBackend().createConquest(conquest);
                        HCF.getInstance().getBackend().createFaction(faction);
                        p.sendMessage(Locale.COMMAND_CONQUEST_CREATED.toString().replace("%conquest%", conquest.getName()));
                    }
                    return;
                }

                if(Conquest.getConquest(args.getArg(0)) == null) {
                    p.sendMessage(Locale.COMMAND_CONQUEST_DOESNT_EXIST.toString().replace("%conquest%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_CONQUEST_DOESNT_EXIST_SUBTEXT.toString().replace("%conquest%", args.getArg(0)));
                    return;
                }

                conquest = Conquest.getConquest(args.getArg(0));

                switch(args.getArg(1).toLowerCase()) {
                    case "delete":
                        conquest.stop();
                        ConquestFaction faction = conquest.getFaction();
                        if(faction != null) {
                            faction.removeClaims();
                            HCF.getInstance().getBackend().deleteFaction(conquest.getFaction());
                            Faction.getFactions().remove(faction.getUuid().toString());
                        }

                        HCF.getInstance().getBackend().deleteConquest(conquest);
                        Conquest.getConquests().remove(conquest.getName());
                        p.sendMessage(Locale.COMMAND_CONQUEST_DELETED.toString().replace("%conquest%", conquest.getName()));
                        return;
                    case "start":
                        conquest.start();
                        return;
                    case "stop":
                        conquest.stop();
                        return;
                    case "setarea":
                        p.sendMessage(Locale.COMMAND_CONQUEST_SETAREA_USAGE.toString());
                        return;
                    case "setpoints":
                        p.sendMessage(Locale.COMMAND_CONQUEST_SETPOINTS_USAGE.toString());
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            case 3:
                switch(args.getArg(1).toLowerCase()) {
                    case "setarea":
                        p.sendMessage(Locale.COMMAND_CONQUEST_SETAREA_USAGE.toString());
                        return;
                    case "setpoints":
                        p.sendMessage(Locale.COMMAND_CONQUEST_SETPOINTS_USAGE.toString());
                        return;
                    default:
                        this.sendUsage(p);
                }
                return;
            case 4:
                if(Conquest.getConquest(args.getArg(0)) == null) {
                    p.sendMessage(Locale.COMMAND_CONQUEST_DOESNT_EXIST.toString().replace("%conquest%", args.getArg(0)));
                    p.sendMessage(Locale.COMMAND_CONQUEST_DOESNT_EXIST_SUBTEXT.toString().replace("%conquest%", args.getArg(0)));
                    return;
                }

                conquest = Conquest.getConquest(args.getArg(0));

                switch(args.getArg(1).toLowerCase()) {
                    case "setarea":
                        String rawPoint = args.getArg(2);
                        String rawArea = args.getArg(3);

                        try {
                            ConquestZoneType.valueOf(rawPoint.toUpperCase());
                        } catch(Exception ex) {
                            p.sendMessage(Locale.COMMAND_CONQUEST_SETAREA_INVALID_ZONE.toString().replace("%zone%", rawPoint));
                            return;
                        }

                        if(ConquestZoneType.valueOf(rawPoint.toUpperCase()) != null) {
                            ConquestZoneType type = ConquestZoneType.valueOf(rawPoint.toUpperCase());

                            try {
                                int area = Integer.parseInt(rawArea);
                                ConquestZone zone = conquest.getZones().get(type);

                                if(area == 1 || area == 2) {
                                    if(area == 1) {
                                        zone.setCorner1(p.getLocation());
                                        zone.reformCuboid();
                                        p.sendMessage(Locale.COMMAND_CONQUEST_SETAREA_ONE.toString()
                                                .replace("%zonetype%", type.getColor() + type.toString())
                                                .replace("%conquest%", conquest.getName())
                                        );
                                    } else {
                                        zone.setCorner2(p.getLocation());
                                        zone.reformCuboid();
                                        p.sendMessage(Locale.COMMAND_CONQUEST_SETAREA_TWO.toString()
                                                .replace("%zonetype%", type.getColor() + type.toString())
                                                .replace("%conquest%", conquest.getName())
                                        );
                                    }
                                    conquest.save();
                                } else {
                                    p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawArea));
                                }
                            } catch(NumberFormatException e) {
                                p.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", rawArea));
                            }
                        } else {
                            p.sendMessage(Locale.COMMAND_CONQUEST_SETAREA_INVALID_ZONE.toString().replace("%zone%", rawPoint));
                        }
                        return;
                    case "setpoints":
                        String rawFaction = args.getArg(2);
                        String rawPoints = args.getArg(3);

                        Faction fac = Faction.getByName(rawFaction);

                        if(fac != null) {
                            if(fac instanceof PlayerFaction) {
                                PlayerFaction pf = (PlayerFaction) fac;

                                try {
                                    int points = Integer.parseInt(rawPoints);

                                    conquest.getPoints().put(pf.getUuid().toString(), points);
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

    @Completer(label = "conquest")
    public List<String> onComplete(CommandData args) {
        List<String> list = new ArrayList<>();
        if(args.length() == 1)
            list.addAll(Conquest.getConquests().keySet());
        if(args.length() == 2)
            list.addAll(Arrays.asList("create", "delete", "start", "stop"));
        return list;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Locale.COMMAND_CONQUEST_USAGE.toString());
    }
}
