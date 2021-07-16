package me.hulipvp.hcf.ui.sidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class PlayerBoard {

    private final List<SidebarEntry> entries = new ArrayList<>();
    private final List<String> identifiers = new ArrayList<>();
    private Scoreboard scoreboard;
    private Objective objective;
    private Sidebar sidebar;

    PlayerBoard(Player player, Sidebar sidebar) {
        this.sidebar = sidebar;
        this.setup(player);
    }

    private void setup(Player player) {
        if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
            this.scoreboard = player.getScoreboard();
        } else {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(sidebar.getTitle());
        player.setScoreboard(this.scoreboard);
    }

    SidebarEntry getEntryAtPosition(int pos) {
        if (pos >= this.entries.size()) {
            return null;
        } else {
            return this.entries.get(pos);
        }
    }

    String getUniqueIdentifier() {
        String identifier = getRandomChatColor() + ChatColor.WHITE;
        while (this.identifiers.contains(identifier)) {
            identifier = identifier + getRandomChatColor() + ChatColor.WHITE;
        }
        if (identifier.length() > 16) {
            return this.getUniqueIdentifier();
        }
        this.identifiers.add(identifier);
        return identifier;
    }

    private static String getRandomChatColor() {
        return ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)].toString();
    }

    List<SidebarEntry> getEntries() {
        return entries;
    }

    List<String> getIdentifiers() {
        return identifiers;
    }

    Scoreboard getScoreboard() {
        return scoreboard;
    }

    Objective getObjective() {
        return objective;
    }

}
