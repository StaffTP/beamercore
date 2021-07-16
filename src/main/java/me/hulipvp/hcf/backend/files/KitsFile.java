package me.hulipvp.hcf.backend.files;

import lombok.Getter;
import me.hulipvp.hcf.game.kits.MapKit;
import me.hulipvp.hcf.game.player.data.PlayerInv;
import me.hulipvp.hcf.utils.InvUtils;
import me.hulipvp.hcf.utils.StringUtils;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class KitsFile extends ConfigFile {

    @Getter private Map<String, MapKit> kits = new HashMap<>();

    public KitsFile() {
        super("kits.yml");
    }

    public void init() {
        if(!config.contains("kits")) {
            config.createSection("kits");
            save();
            return;
        }

        for(String kit : config.getConfigurationSection("kits").getKeys(false)) {
            PlayerInv playerInv = InvUtils.invFromString(config.getString("kits." + kit + ".inv"));
            ChatColor color = ChatColor.valueOf(config.getString("kits." + kit + ".color", "GREEN"));
            boolean enabled = config.getBoolean("kits." + kit + ".enabled");

            MapKit mapKit = new MapKit(kit);
            mapKit.setPlayerInv(playerInv);
            mapKit.setColor(color);
            mapKit.setEnabled(enabled);

            kits.put(kit, mapKit);
        }
    }

    public MapKit getKit(String kit) {
        return kits.get(kit);
    }

    public void saveKit(MapKit kit) {
        config.set("kits." + kit.getName() + ".inv", kit.getPlayerInv().toString());
        config.set("kits." + kit.getName() + ".color", kit.getColor().name());
        config.set("kits." + kit.getName() + ".enabled", kit.isEnabled());
    }

    public void saveKits() {
        getKits().values().forEach(this::saveKit);

        this.save();
    }

    public boolean isValidKit(String kit) {
        return getKit(kit) != null;
    }

    public void setEnabled(String kit, boolean enabled) {
        if(isValidKit(kit)) {
            getKit(kit).setEnabled(enabled);
        }

        config.set("kits." + kit + ".enabled", enabled);
        this.save();
    }

    public boolean isEnabled(String kit) {
        return getKit(kit).isEnabled();
    }

    public void setMapkitInv(String kit, PlayerInv playerInv) {
        if(isValidKit(kit)) {
            getKit(kit).setPlayerInv(playerInv);
        }

        config.set("kits." + kit + ".inv", playerInv.toString());
        this.save();
    }

    public PlayerInv getMapkitInv(String kit) {
        return getKit(kit).getPlayerInv();
    }

    public void setColor(String kit, ChatColor color) {
        if(isValidKit(kit)) {
            getKit(kit).setColor(color);
        }

        config.set("kits." + kit + ".color", StringUtils.chatColorToString(color));
        this.save();
    }

    public ChatColor getColor(String kit) {
        ChatColor color;
        try {
            color = ChatColor.valueOf(config.getString("kits." + kit + ".color"));
        } catch(Exception ex) {
            color = ChatColor.GREEN;
        }

        return color;
    }

    public String getDisplay(String kit) {
        return getKit(kit).getDisplay();
    }
}
