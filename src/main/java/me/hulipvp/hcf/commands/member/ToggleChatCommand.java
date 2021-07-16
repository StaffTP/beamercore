package me.hulipvp.hcf.commands.member;

import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.ChatMode;
import me.hulipvp.hcf.game.player.data.setting.SettingType;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.entity.Player;

public class ToggleChatCommand {

    @Command(label = "tgc", aliases = { "toggleglobalchat" }, playerOnly = true)
    public void onToggleGlobal(CommandData args) {
        HCFProfile profile = HCFProfile.getByPlayer(args.getPlayer());

        profile.getSetting(SettingType.PUBLIC_CHAT).toggle();
        if(profile.getSetting(SettingType.PUBLIC_CHAT).isValue())
            args.getPlayer().sendMessage(Locale.COMMAND_CHAT_ENABLED.toString());
        else
            args.getPlayer().sendMessage(Locale.COMMAND_CHAT_DISABLED.toString());
    }

    @Command(label = "gc", aliases = { "globalchat" }, playerOnly = true)
    public void onGlobalChat(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        profile.setChatMode(ChatMode.PUBLIC);
        player.sendMessage(Locale.COMMAND_FACTION_CHAT_CHANGED.toString().replace("%mode%", ChatMode.PUBLIC.getDisplay()));
    }

    @Command(label = "tc", aliases = { "teamchat" }, playerOnly = true)
    public void onTeamChat(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        if(profile.getFactionObj() != null)
            profile.setChatMode(ChatMode.FACTION);
        else
            profile.setChatMode(ChatMode.PUBLIC);

        player.sendMessage(Locale.COMMAND_FACTION_CHAT_CHANGED.toString().replace("%mode%", ChatMode.FACTION.getDisplay()));
    }

    @Command(label = "ac", aliases = { "allychat" }, playerOnly = true)
    public void onAllyChat(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        if(profile.getFactionObj() != null)
            profile.setChatMode(ChatMode.ALLY);
        else
            profile.setChatMode(ChatMode.PUBLIC);

        player.sendMessage(Locale.COMMAND_FACTION_CHAT_CHANGED.toString().replace("%mode%", ChatMode.ALLY.getDisplay()));
    }

    @Command(label = "oc", aliases = { "officerchat" }, playerOnly = true)
    public void onOfficerChat(CommandData args) {
        Player player = args.getPlayer();
        HCFProfile profile = HCFProfile.getByPlayer(player);

        if(profile.getFactionObj() != null && profile.getFactionObj().getMembers().get(profile.getUuid()).isAtLeast(FactionRank.CAPTAIN))
            profile.setChatMode(ChatMode.CAPTAIN);
        else
            profile.setChatMode(ChatMode.PUBLIC);

        player.sendMessage(Locale.COMMAND_FACTION_CHAT_CHANGED.toString().replace("%mode%", ChatMode.CAPTAIN.getDisplay()));
    }
}
