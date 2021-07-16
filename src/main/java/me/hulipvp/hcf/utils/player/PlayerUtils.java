package me.hulipvp.hcf.utils.player;

import lombok.Getter;
import me.hulipvp.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtils {

    @Getter private static final Map<String, UUIDPair> pairs = new HashMap<>();

    public static UUIDPair getExtraInfo(String name)
            throws IOException, ParseException {
        if(pairs.containsKey(name.toLowerCase()))
            return pairs.get(name.toLowerCase());

        Player target = Bukkit.getPlayerExact(name);
        if(target != null && target.isOnline())
            return new UUIDPair(target.getUniqueId(), target.getName());

        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(name);
        if(offlineTarget.hasPlayedBefore())
            return new UUIDPair(offlineTarget.getUniqueId(), offlineTarget.getName());

        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(reader.readLine());
        UUID uuid = UUID.fromString(String.valueOf(obj.get("id")).replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));

        name = String.valueOf(obj.get("name"));
        reader.close();

        return new UUIDPair(uuid, name);
    }

    public static boolean hasHook() {
        return HCF.getInstance().getPlayerHook() != null;
    }

    public static String getRankName(Player player) {
        if(HCF.getInstance().getPlayerHook() == null)
            return "";

        return HCF.getInstance().getPlayerHook().getRankName(player);
    }

    public static String getRankPrefix(Player player) {
        if(HCF.getInstance().getPlayerHook() == null)
            return "";

        return HCF.getInstance().getPlayerHook().getRankPrefix(player);
    }

    public static String getRankSuffix(Player player) {
        if(HCF.getInstance().getPlayerHook() == null)
            return "";

        return HCF.getInstance().getPlayerHook().getRankSuffix(player);
    }
}
