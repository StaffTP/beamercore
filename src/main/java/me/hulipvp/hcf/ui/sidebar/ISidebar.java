package me.hulipvp.hcf.ui.sidebar;

import org.bukkit.entity.Player;

import java.util.List;

public interface ISidebar {
    List<String> getLines(Player player);
}
