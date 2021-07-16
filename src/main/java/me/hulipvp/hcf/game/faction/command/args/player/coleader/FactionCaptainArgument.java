package me.hulipvp.hcf.game.faction.command.args.player.coleader;

import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.faction.type.player.FactionMember;
import me.hulipvp.hcf.game.faction.type.player.FactionRank;
import me.hulipvp.hcf.game.faction.type.player.PlayerFaction;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.utils.ConfigValues;
import me.hulipvp.hcf.utils.Locale;
import me.hulipvp.hcf.utils.chat.C;
import me.hulipvp.hcf.utils.command.Command;
import me.hulipvp.hcf.utils.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionCaptainArgument {

    @Command(label = "f.captain", aliases = { "f.officer" }, playerOnly = true)
    public void onCommand(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        this.sendUsage(p);
    }

    @Command(label = "f.captain.list", aliases = { "f.officer.list" }, playerOnly = true)
    public void onCaptainList(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        PlayerFaction pf = profile.getFactionObj();
        FactionMember pMember = pf.getMembers().get(p.getUniqueId());
        if(!pMember.isAtLeast(FactionRank.COLEADER)) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_COLEADER.toString());
            return;
        }

        long captains = pf.getMembers().values().stream().filter(member -> member.getRank() == FactionRank.CAPTAIN).count();
        p.sendMessage(C.color("&eFaction Captains: " + (captains == 0 ? "&cNone" : ""))); // TODO: ADD ALL TO LOCALE
        if(captains == 0)
            return;

        int count = 1;
        for(FactionMember member : pf.getMembers().values()) {
            if(member.getRank() == FactionRank.CAPTAIN) {
                p.sendMessage(C.color("&a" + count + ". &e" + Bukkit.getOfflinePlayer(member.getUuid()).getName()));
                ++count;
            }
        }
    }

    @Command(label = "f.captain.add", aliases = { "f.officer.add" }, playerOnly = true)
    public void onCaptainAdd(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        PlayerFaction pf = profile.getFactionObj();
        FactionMember pMember = pf.getMembers().get(p.getUniqueId());
        if(!pMember.isAtLeast(FactionRank.COLEADER)) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_COLEADER.toString());
            return;
        }

        if(args.length() != 3) {
            this.sendUsage(p);
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(2));
            if(target.isOnline() || target.hasPlayedBefore()) {
                if(pf.getMembers().containsKey(target.getUniqueId())) {
                    FactionMember member = pf.getMembers().get(target.getUniqueId());

                    if(member.getRank() == FactionRank.MEMBER || member.getRank() == FactionRank.COLEADER) {
                        member.setRank(FactionRank.CAPTAIN);

                        p.sendMessage(Locale.COMMAND_FACTION_CAPTAIN_SUCCESS.toString().replace("%player%", target.getName()));
                        if(target.isOnline())
                            ((Player) target).sendMessage(Locale.COMMAND_FACTION_CAPTAIN_PROMOTED.toString());
                    } else {
                        p.sendMessage(Locale.COMMAND_FACTION_CHANGE_INVALID.toString());
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_PLAYER_NOT_IN.toString().replace("%name%", args.getArg(2)));
                }
            } else {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(2)));
            }
        }
    }

    @Command(label = "f.captain.remove", aliases = { "f.officer.remove" }, playerOnly = true)
    public void onCaptainRemove(CommandData args) {
        Player p = (Player) args.getSender();
        HCFProfile profile = HCFProfile.getByPlayer(p);

        if(profile.getFactionObj() == null) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_IN.toString());
            return;
        }

        PlayerFaction pf = profile.getFactionObj();
        FactionMember pMember = pf.getMembers().get(p.getUniqueId());
        if(!pMember.isAtLeast(FactionRank.COLEADER)) {
            p.sendMessage(Locale.COMMAND_FACTION_NOT_COLEADER.toString());
            return;
        }

        if(args.length() != 3) {
            this.sendUsage(p);
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args.getArg(2));
            if(target.isOnline() || target.hasPlayedBefore()) {
                if(pf.getMembers().containsKey(target.getUniqueId())) {
                    FactionMember member = pf.getMembers().get(target.getUniqueId());

                    if(member.getRank() == FactionRank.CAPTAIN) {
                        member.setRank(FactionRank.MEMBER);

                        p.sendMessage(Locale.COMMAND_FACTION_CAPTAIN_SUCCESS_DEMOTE.toString().replace("%player%", target.getName()));
                        if(target.isOnline())
                            ((Player) target).sendMessage(Locale.COMMAND_FACTION_CAPTAIN_DEMOTED.toString());
                    } else {
                        p.sendMessage(Locale.COMMAND_FACTION_CHANGE_INVALID.toString());
                    }
                } else {
                    p.sendMessage(Locale.COMMAND_FACTION_PLAYER_NOT_IN.toString().replace("%name%", args.getArg(2)));
                }
            } else {
                p.sendMessage(Locale.PLAYER_NOT_FOUND.toString().replace("%name%", args.getArg(2)));
            }
        }
    }

    private void sendUsage(Player player) {
        for(String str : HCF.getInstance().getMessagesFile().getFactionCaptainHelp())
            player.sendMessage(C.color(str
                    .replace("%primary%", ConfigValues.SERVER_PRIMARY)
                    .replace("%secondary%", ConfigValues.SERVER_SECONDARY))
            );
    }
}
