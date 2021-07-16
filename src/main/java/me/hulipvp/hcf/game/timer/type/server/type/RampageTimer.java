package me.hulipvp.hcf.game.timer.type.server.type;

import lombok.Getter;
import me.hulipvp.hcf.game.timer.type.server.ServerTimer;
import me.hulipvp.hcf.game.timer.type.server.ServerTimerType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RampageTimer extends ServerTimer {


    public RampageTimer() {
        super(ServerTimerType.RAMPAGE);
        kills = new HashMap<>();
    }

    @Getter
    private Map<String, Integer> kills;

    public Integer getPlayerKills(Player player) {
        return this.kills.getOrDefault(player.getName(), 0);
    }

    public LinkedHashMap<String, Integer> getSortedMap() {
        return kills
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public String getTopKiller() {
        if (getSortedMap().isEmpty() || getSortedMap().keySet().toArray().length == 0) return null;
        return getSortedMap().keySet().stream().findFirst().orElse(null);
    }

    public int getTopKillerKills() {
        if (getSortedMap().isEmpty() || getSortedMap().values().toArray().length == 0) return 0;
        return getSortedMap().values().stream().findFirst().orElse(0);
    }


}
