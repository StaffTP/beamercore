package me.hulipvp.hcf.game.faction.type.player;

import lombok.Getter;

import java.util.Arrays;

public enum FactionRank {

    LEADER(4),
    COLEADER(3),
    CAPTAIN(2),
    MEMBER(1);

    @Getter private final int rank;

    FactionRank(int rank) {
        this.rank = rank;
    }

    public static FactionRank getByString(String string) {
        return Arrays.stream(values())
                .filter(rank -> rank.name().equalsIgnoreCase(string))
                .findFirst()
                .orElse(null);
    }
}
