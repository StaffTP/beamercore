package me.hulipvp.hcf.utils.player;

import java.util.UUID;

public class UUIDPair extends Pair<UUID, String> {

    public UUIDPair(UUID key, String value) {
        super(key, value);

        PlayerUtils.getPairs().put(value.toLowerCase(), this);
    }
}
