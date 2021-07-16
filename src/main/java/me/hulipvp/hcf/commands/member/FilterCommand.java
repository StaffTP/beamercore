package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class FilterCommand {

    // TODO: Make the filter show up through a GUI

    @Command(label = "filter", playerOnly = true)
    public void onFilter(CommandData args) {
        Player player = args.getPlayer();

        sendUsage(player);
    }

    @Command(label = "filter.add", playerOnly = true)
    public void onFilterAdd(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        if(args.length() != 2) {
            sendUsage(player);
        } else {
            Material material = Material.getMaterial(args.getArg(1).toUpperCase());
            if(material == null) {
                player.sendMessage(Locale.COMMAND_FILTER_INVALID_MATERIAL.toString());
                return;
            }

            if(profile.getFiltered().contains(material)) {
                player.sendMessage(Locale.COMMAND_FILTER_ALREADY_ADDED.toString());
                return;
            }

            profile.getFiltered().add(material);
            profile.save();
            player.sendMessage(Locale.COMMAND_FILTER_ADDED_MATERIAL.toString().replace("%type%", WordUtils.capitalizeFully(material.name().replace("_", " "))));
        }
    }

    @Command(label = "filter.remove", playerOnly = true)
    public void onFilterRemove(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        if(args.length() != 2) {
            sendUsage(player);
        } else {
            Material material = Material.getMaterial(args.getArg(1).toUpperCase());
            if(material == null) {
                player.sendMessage(Locale.COMMAND_FILTER_INVALID_MATERIAL.toString());
                return;
            }

            if(profile.getFiltered().remove(material)) {
                profile.save();
                player.sendMessage(Locale.COMMAND_FILTER_REMOVED_MATERIAL.toString().replace("%type%", WordUtils.capitalizeFully(material.name().replace("_", " "))));
            } else {
                player.sendMessage(Locale.COMMAND_FILTER_MATERIAL_NOT_ADDED.toString());
            }
        }
    }

    @Command(label = "filter.toggle", playerOnly = true)
    public void onFilterToggle(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        profile.setFilterEnabled(!profile.isFilterEnabled());
        player.sendMessage(Locale.COMMAND_FILTER_TOGGLED.toString().replace("%status%", profile.isFilterEnabled() ? C.color("&aEnabled") : C.color("&cDisabled")));
    }

    @Command(label = "filter.list", playerOnly = true)
    public void onFilterList(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        List<Material> filtered = profile.getFiltered();
        player.sendMessage(C.color("&eFiltered Items:" + (filtered.size() == 0 ? " &cNone" : "")));
        if(filtered.size() > 0) {
            for(Material material : filtered)
                player.sendMessage(C.color(" &7- &a" + WordUtils.capitalizeFully(material.name().replace("_", " "))));
        }
    }

    @Command(label = "cobble", playerOnly = true)
    public void onCobble(CommandData args) {
        toggleMaterial(args.getPlayer(), Material.COBBLESTONE);
    }

    @Command(label = "stone", playerOnly = true)
    public void onStone(CommandData args) {
        toggleMaterial(args.getPlayer(), Material.STONE);
    }

    private void toggleMaterial(Player player, Material material) {
        HCFProfile profile = HCFProfile.getByPlayer(player);

        if(profile.getFiltered().remove(material)) {
            profile.save();
            player.sendMessage(Locale.COMMAND_FILTER_REMOVED_MATERIAL.toString().replace("%type%", WordUtils.capitalizeFully(material.name().replace("_", " "))));
        } else {
            profile.getFiltered().add(material);
            player.sendMessage(Locale.COMMAND_FILTER_ADDED_MATERIAL.toString().replace("%type%", WordUtils.capitalizeFully(material.name().replace("_", " "))));
        }
        profile.setFilterEnabled(true);
    }

    private void sendUsage(Player player) {
        for(String str : HCF.getInstance().getMessagesFile().getFilterHelp())
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
