package me.hulipvp.hcf.ui.sidebar;

import me.hulipvp.hcf.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Sidebar implements Listener {

    private Plugin plugin;
    private String title;
    private ISidebar iSidebar;
    private Map<UUID, PlayerBoard> boards;
    private SidebarStyle sidebarStyle;
    private int updateTaskId;

    private static final ISidebar EMPTY_LINES = (player) -> new ArrayList<>();

    public Sidebar(String title) {
        this.title = title;
        this.iSidebar = EMPTY_LINES;
        this.sidebarStyle = SidebarStyle.getModernStyle();
    }

    public Sidebar setLines(ISidebar lines) {
        this.iSidebar = lines;
        return this;
    }

    public Sidebar setStyle(SidebarStyle style) {
        this.sidebarStyle = style;
        return this;
    }

    public void build(Plugin plugin) {
        this.plugin = plugin;
        this.boards = new ConcurrentHashMap<>();
        this.setup();
    }

    private void setup() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        if (this.updateTaskId != -1) {
            this.plugin.getServer().getScheduler().cancelTask(updateTaskId);
        }
        this.boards.clear();
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            this.boards.put(player.getUniqueId(), new PlayerBoard(player, this));
        });
        this.start();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        this.boards.put(event.getPlayer().getUniqueId(), new PlayerBoard(event.getPlayer(), this));
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        this.boards.remove(event.getPlayer().getUniqueId());
        event.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        Bukkit.shutdown();
    }

    String getTitle() {
        return title;
    }

    private void start() {
        this.updateTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if(player == null ) {
                    return;
                }
                PlayerBoard board = boards.get(player.getUniqueId());
                if(board == null) {
                    return;
                }
                Objective objective = board.getObjective();
                String title = CC.translate(this.title).replace("%verticalLine%", "❘");
                if (!objective.getDisplayName().equals(title)) {
                    objective.setDisplayName(title);
                }
                List<String> newLines = this.iSidebar.getLines(player);
                if (newLines == null || newLines.isEmpty()) {
                    board.getEntries().forEach(SidebarEntry::remove);
                    board.getEntries().clear();
                } else {
                    if(!this.sidebarStyle.isDescend()) {
                        Collections.reverse(newLines);
                    }
                    if (board.getEntries().size() > newLines.size()) {
                        for (int i = newLines.size(); i < board.getEntries().size(); i++) {
                            SidebarEntry entry = board.getEntryAtPosition(i);
                            if (entry != null) {
                                entry.remove();
                            }
                        }
                    }
                    int cache = this.sidebarStyle.getStartNumber();
                    for (int i = 0; i < newLines.size(); i++) {
                        SidebarEntry entry = board.getEntryAtPosition(i);
                        String line = CC.translate(newLines.get(i));
                        if (entry == null) {
                            entry = new SidebarEntry(board, line);
                        }
                        entry.setText(line);
                        entry.setup();
                        entry.send(sidebarStyle.isDescend() ? cache-- : cache++);
                    }
                }
            });
        }, 2L, 2L).getTaskId();
    }

}
