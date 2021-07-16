package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;

public class HelpCommand {

    @Command(label = "help", aliases = { "?" })
    public void onHelp(CommandData args) {
        for(String str : HCF.getInstance().getMessagesFile().getHelp())
            args.getSender().sendMessage(C.color(replace(str)));
    }

    @Command(label = "coords")
    public void onCoords(CommandData args) {
        for(String str : HCF.getInstance().getMessagesFile().getCoords())
            args.getSender().sendMessage(C.color(replace(str)));
    }

    private String replace(String string) {
        return string
                .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                .replace("%secondary%", ConfigValues.SERVER_SECONDARY)
                .replace("%servername%", ConfigValues.SERVER_NAME)
                .replace("%servernamelower%", ConfigValues.SERVER_NAME.toLowerCase())
                .replace("%website%", ConfigValues.SERVER_WEBSITE)
                .replace("%teamspeak%", ConfigValues.SERVER_TEAMSPEAK)
                .replace("%store%", ConfigValues.SERVER_STORE)
                .replace("%mapstartdate%", ConfigValues.MAP_START_DATE)
                .replace("%border%", ConfigValues.LIMITERS_WORLD_BORDER.toString())
                .replace("%warzone%", ConfigValues.FACTIONS_SIZE_WARZONE.toString())
                .replace("%map%", ConfigValues.MAP_NUMBER.toString());

    }
}
