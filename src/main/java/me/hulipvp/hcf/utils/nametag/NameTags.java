package me.hulipvp.hcf.utils.nametag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;

public class NameTags {

	private static final String PREFIX = "nt_team_";
	private static final ChatColor[] COLORS = new ChatColor[]{
			ChatColor.RED,
			ChatColor.GREEN,
			ChatColor.BLUE,
			ChatColor.AQUA,
			ChatColor.LIGHT_PURPLE,
			ChatColor.DARK_PURPLE,
			ChatColor.GOLD,
			ChatColor.YELLOW,
	};

	public static void setup(Player player) {
		Scoreboard scoreboard = player.getScoreboard();

		if (scoreboard.equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard())) {
			scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		}

		for (ChatColor color : COLORS) {
			String teamName = getTeamName(color);
			Team team = scoreboard.getTeam(teamName);

			if (team == null) {
				team = scoreboard.registerNewTeam(teamName);
			}

			team.setPrefix(color.toString());

			Iterator<String> entryIterator = team.getEntries().iterator();

			while (entryIterator.hasNext()) {
				entryIterator.remove();
			}
		}

		player.setScoreboard(scoreboard);
	}

	public static void color(Player player, Player other, ChatColor color) {
		if (player.equals(other)) {
			return;
		}

		Team team = player.getScoreboard().getTeam(getTeamName(color));

		if (team == null) {
			team = player.getScoreboard().registerNewTeam(getTeamName(color));
			team.setPrefix(color.toString());
		}

		if (!team.hasEntry(other.getName())) {
			reset(player, other);

			team.addEntry(other.getName());
		}
	}

	public static void invis(Player player, Player other) {
		if (player != null && other != null && !player.equals(other)) {
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

			if (objective != null) {
				objective.unregister();
			}

			for (Team team : player.getScoreboard().getTeams()) {
				team.removeEntry(other.getName());
			}
		}
	}

	public static void reset(Player player, Player other) {
		if (player != null && other != null && !player.equals(other)) {
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

			if (objective != null) {
				objective.unregister();
			}

			for (Team team : player.getScoreboard().getTeams()) {
				team.removeEntry(other.getName());
			}
		}
	}

	private static String getTeamName(ChatColor color) {
		return PREFIX + color.ordinal();
	}

}
