package me.hulipvp.hcf.game.player.data;

import lombok.Getter;

import java.util.UUID;

public class Kill {

    @Getter private final UUID uuid, killed;
    @Getter private final String type;
    @Getter private final long time;

    public Kill(UUID uuid, UUID killed, String type) {
        this.uuid = uuid;
        this.killed = killed;
        this.type = type;
        this.time = System.currentTimeMillis();
    }

    public Kill(UUID uuid, UUID killed) {
        this(uuid, killed, "");
    }
}
