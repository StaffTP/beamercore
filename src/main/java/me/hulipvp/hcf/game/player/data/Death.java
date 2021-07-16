package me.hulipvp.hcf.game.player.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class Death {
    
    @Getter private final UUID randomUuid, uuid, killer;
    @Getter private final Location location;
    @Getter private final PlayerInv inv;
    @Getter private final long time;

    public Death(UUID uuid, UUID killer, Location location, PlayerInv inv) {
        this.randomUuid = UUID.randomUUID();
        this.uuid = uuid;
        this.killer = killer;
        this.location = location;
        this.inv = inv;
        this.time = System.currentTimeMillis();
    }

    public Death(UUID uuid, UUID killer) {
        this(uuid, killer, Bukkit.getPlayer(uuid).getLocation(), PlayerInv.fromPlayer(Bukkit.getPlayer(uuid).getInventory()));
    }
}
