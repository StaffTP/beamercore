package me.hulipvp.hcf.game.event.mountain.command;

import me.hulipvp.hcf.game.faction.type.event.MountainFaction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.event.mountain.Mountain;
import me.hulipvp.hcf.game.event.mountain.type.MountainType;
import me.hulipvp.hcf.game.faction.Faction;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.StringUtils;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class MountainCommand {

    @Command(label = "mountain", permission = "command.mountain", playerOnly = true)
    public void onCommand(CommandData args) {
        Player player = (Player) args.getSender();
        Mountain mountain;

        switch(args.length()) {
            case 1:
                if(Mountain.getMountain(args.getArg(0)) != null)
                    player.sendMessage(Locale.COMMAND_MOUNTAIN_EXISTS.toString());
                else
                    player.sendMessage(Locale.COMMAND_MOUNTAIN_USAGE.toString());
                return;
            case 2:
                if(args.getArg(1).equalsIgnoreCase("create")) {
                    if(Mountain.getMountain(args.getArg(0)) != null) {
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_EXISTS.toString());
                        return;
                    }
                }

                if(Mountain.getMountain(args.getArg(0)) == null) {
                    player.sendMessage(Locale.COMMAND_MOUNTAIN_DOESNT_EXIST.toString().replace("%mountain%", args.getArg(0)));
                    return;
                }

                mountain = Mountain.getMountain(args.getArg(0));

                switch(args.getArg(1).toLowerCase()) {
                    case "delete":
                        MountainFaction faction = mountain.getFaction();
                        if(faction != null) {
                            faction.removeClaims();
                            HCF.getInstance().getBackend().deleteFaction(faction);
                            Faction.getFactions().remove(faction.getUuid().toString());
                        }

                        HCF.getInstance().getBackend().deleteFaction(mountain.getFaction());
                        HCF.getInstance().getBackend().deleteMountain(mountain);
                        Mountain.getMountains().remove(mountain.getName());
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_REMOVED.toString().replace("%mountain%", mountain.getName()));
                        return;
                    case "reset":
                        mountain.reset(); // No need to send message cuz of reset broadcast
                        return;
                    case "setpos1":
                        mountain.setPoint1(player.getLocation());
                        mountain.save();
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_SETPOS1.toString().replace("%mountain%", mountain.getName()));
                        return;
                    case "setpos2":
                        mountain.setPoint2(player.getLocation());
                        mountain.save();
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_SETPOS2.toString().replace("%mountain%", mountain.getName()));
                        return;
                    default:
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_USAGE.toString());
                        return;
                }
            case 3:
                switch(args.getArg(1).toLowerCase()) {
                    case "create": {
                        if(Mountain.getMountain(args.getArg(0)) != null) {
                            player.sendMessage(Locale.COMMAND_MOUNTAIN_EXISTS.toString());
                            return;
                        }

                        MountainType type;
                        try {
                            type = MountainType.valueOf(args.getArg(2).toUpperCase());
                        } catch(Exception ex) {
                            player.sendMessage(Locale.COMMAND_MOUNTAIN_INVALID_TYPE.toString());
                            return;
                        }

                        mountain = new Mountain(args.getArg(0), type);
                        if(Faction.getByName(args.getArg(0)) != null) {
                            Faction oldFaction = Faction.getByName(args.getArg(0));
                            HCF.getInstance().getBackend().deleteFaction(oldFaction);
                            Faction.getFactions().remove(oldFaction.getUuid().toString());
                        }

                        MountainFaction mountainFaction = new MountainFaction(null, mountain.getName(), mountain);
                        HCF.getInstance().getBackend().createMountain(mountain);
                        HCF.getInstance().getBackend().createFaction(mountainFaction);

                        mountain.setFaction(mountainFaction);

                        Mountain.getMountains().put(mountain.getName(), mountain);
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_CREATED.toString().replace("%mountain%", mountain.getName()));
                        break;
                    }
                    case "setreset": {
                        if(Mountain.getMountain(args.getArg(0)) == null) {
                            player.sendMessage(Locale.COMMAND_MOUNTAIN_DOESNT_EXIST.toString().replace("%mountain%", args.getArg(0)));
                            return;
                        }

                        mountain = Mountain.getMountain(args.getArg(0));

                        String unparsed = args.getArg(2);
                        if(!StringUtils.isInt(unparsed)) {
                            player.sendMessage(Locale.INVALID_NUMBER.toString().replace("%number%", unparsed));
                            return;
                        }

                        Integer time = Integer.parseInt(unparsed);
                        mountain.setResetTime(time);
                        mountain.save();
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_SET_TIME.toString().replace("%mountain%", mountain.getName()).replace("%time%", unparsed));
                        break;
                    }
                    default:
                        player.sendMessage(Locale.COMMAND_MOUNTAIN_USAGE.toString());
                }
                return;
            default:
                player.sendMessage(Locale.COMMAND_MOUNTAIN_USAGE.toString());
        }
    }
}
