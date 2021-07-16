package me.hulipvp.hcf.game.player.data.mod;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.player.data.mod.item.ModItems;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.game.player.HCFProfile;
import me.hulipvp.hcf.game.player.data.PlayerInv;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Vanish {

    public static final String teamName = "hidden";

    @Getter private final Player player;
    @Getter private boolean vanished;
    @Getter private boolean modMode;
    @Getter @Setter private boolean bypass;

    @Getter @Setter private PlayerInv inv;

    public Vanish(Player player) {
        this.player = player;
        this.vanished = false;
        this.modMode = false;
    }

    public void enable() {
        // Setting the player to vanished.
        this.setVanished(true);
        this.modMode = true;

        // Setting items into arrays for later use when mod-mode is disabled.
        this.inv = PlayerInv.fromPlayer(this.player.getInventory());
        this.inv.setGameMode(this.player.getGameMode());

        // Updating player's inventory items.
        this.refreshItems();

        // Setting the player to Creative Mode.
        this.player.setGameMode(GameMode.CREATIVE);
        this.player.setAllowFlight(true);
    }

    public void disable() {
        this.setVanished(false);
        this.modMode = false;


        if(this.inv != null) {
            this.inv.load(this.player);

            this.player.setGameMode(inv.getGameMode());
        }
    }

    public boolean canBypass() {
        boolean bypass = true;
        if (!isVanished() && !isModMode()) {
            bypass = true;
        } else if (isVanished() && !HCF.getInstance().getModModeFile().getConfig().getBoolean("mod-mode.mod-bypass-also-works-in-vanish")) {
            bypass = false;
        } else if ((isVanished() || isModMode()) && !isBypass()) {
            bypass = false;
        }
        return bypass;
    }

    public void refreshItems() {
        // Clearing the player's entire inventory so that their items don't interfere with the mod-mode ones.
        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(null);

        // Setting the player's inventory to the mod-mode inventory.
        ModItems.getItemsFor(player).forEach(this.player.getInventory()::setItem);

        // Updating the player's inventory.
        this.player.updateInventory();
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;

        if(vanished) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!player.hasPermission("hcf.staff")) {
                    if(player.getName().equals(this.player.getName()))
                        continue;

                    player.hidePlayer(this.player);
                } else {
                    Scoreboard scoreboard = player.getScoreboard();
                    if(scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        player.setScoreboard(scoreboard);
                    }

                    Team team = scoreboard.getTeam(teamName);
                    if(team == null) {
                        team = scoreboard.registerNewTeam(teamName);
                        team.setPrefix(ChatColor.GRAY.toString());
                        team.setCanSeeFriendlyInvisibles(true);
                    }

                    team.addEntry(this.player.getName());


                    HCFProfile staff = HCFProfile.getByPlayer(player);
                    if (staff.isHideStaff()) {
                        player.hidePlayer(this.player);
                    } else {
                        player.showPlayer(this.player);
                    }
                }
            }
        } else {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(this.player);

                Scoreboard scoreboard = player.getScoreboard();
                if(!scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                    Team team = scoreboard.getTeam(teamName);
                    if(team == null)
                        continue;

                    team.removeEntry(this.player.getName());
                }
            }
        }
    }
}
