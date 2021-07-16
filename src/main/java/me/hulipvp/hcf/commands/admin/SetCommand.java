package me.hulipvp.hcf.commands.admin;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class SetCommand {

    @Command(label = "setendspawn", permission = "command.setendspawn", playerOnly = true)
    public void onSetEndSpawn(CommandData args) {
        Player p = (Player) args.getSender();

        HCF.getInstance().getLocationsFile().setEndSpawn(p.getLocation());
        if(HCF.getInstance().getLocationsFile().save())
            p.sendMessage(Locale.COMMAND_SETENDSPAWN_SUCCESS.toString());
        else
            p.sendMessage(Locale.COMMAND_SETENDSPAWN_FAILURE.toString());
    }

    @Command(label = "setendexit", permission = "command.setendexit", playerOnly = true)
    public void onSetEndExit(CommandData args) {
        Player p = (Player) args.getSender();

        HCF.getInstance().getLocationsFile().setEndExit(p.getLocation());
        if(HCF.getInstance().getLocationsFile().save())
            p.sendMessage(Locale.COMMAND_SETENDEXIT_SUCCESS.toString());
        else
            p.sendMessage(Locale.COMMAND_SETENDEXIT_FAILURE.toString());
    }

    @Command(label = "setnetherspawn", permission = "command.setnetherspawn", playerOnly = true)
    public void onSetNetherSpawn(CommandData args) {
        Player p = (Player) args.getSender();

        HCF.getInstance().getLocationsFile().setNetherSpawn(p.getLocation());
        if(HCF.getInstance().getLocationsFile().save())
            p.sendMessage(Locale.COMMAND_SETNETHERSPAWN_SUCCESS.toString());
        else
            p.sendMessage(Locale.COMMAND_SETNETHERSPAWN_FAILURE.toString());
    }

    @Command(label = "setnetherexit", permission = "command.setnetherexit", playerOnly = true)
    public void onSetNetherExit(CommandData args) {
        Player p = (Player) args.getSender();

        HCF.getInstance().getLocationsFile().setNetherExit(p.getLocation());
        if(HCF.getInstance().getLocationsFile().save())
            p.sendMessage(Locale.COMMAND_SETNETHEREXIT_SUCCESS.toString());
        else
            p.sendMessage(Locale.COMMAND_SETNETHEREXIT_FAILURE.toString());
    }
}
