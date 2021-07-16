package me.hulipvp.hcf.utils;

import me.hulipvp.hcf.game.faction.Faction;
import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public class StringUtils {

    public static String chatColorToString(ChatColor color) {
        switch(color) {
            case BLACK:
                return "BLACK";
            case DARK_BLUE:
                return "DARK_BLUE";
            case DARK_GREEN:
                return "DARK_GREEN";
            case DARK_AQUA:
                return "DARK_AQUA";
            case DARK_RED:
                return "DARK_RED";
            case DARK_PURPLE:
                return "DARK_PURPLE";
            case GOLD:
                return "GOLD";
            case GRAY:
                return "GRAY";
            case DARK_GRAY:
                return "DARK_GRAY";
            case BLUE:
                return "BLUE";
            case GREEN:
                return "GREEN";
            case AQUA:
                return "AQUA";
            case RED:
                return "RED";
            case LIGHT_PURPLE:
                return "LIGHT_PURPLE";
            case YELLOW:
                return "YELLOW";
            case WHITE:
                return "WHITE";
        }
        return null;
    }

    public static String checkFactionName(String name) {
        int min = ConfigValues.FACTIONS_NAME_MIN;
        int max = ConfigValues.FACTIONS_NAME_MAX;
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");

        if(name.length() < min)
            return Locale.FACTION_NAME_TOO_SHORT.toString().replace("%min%", String.valueOf(min));

        if(name.length() > max)
            return Locale.FACTION_NAME_TOO_LONG.toString().replace("%max%", String.valueOf(max));

        if(p.matcher(name).find())
            return Locale.FACTION_NAME_NOT_ALPHANUMERIC.toString();

        if(Faction.getByName(name) != null)
            return Locale.FACTION_NAME_IN_USE.toString();

        if(ConfigValues.FACTIONS_NAME_BLOCKED.stream().anyMatch(name::equalsIgnoreCase))
            return Locale.FACTION_NAME_BLOCKED.toString();

        return null;
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) {
        if(string.length() < prefix.length())
            return false;

        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);

            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}
