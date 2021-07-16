package me.hulipvp.hcf.game.faction.type.player;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class FactionMember {

    @Getter private final UUID uuid;
    @Getter @Setter private FactionRank rank;

    public FactionMember(UUID uuid, FactionRank rank) {
        this.uuid = uuid;
        this.rank = rank;
    }

    public boolean isAtLeast(FactionRank rank) {
        return this.getRank().getRank() >= rank.getRank();
    }

    public String getStars() {
        switch(this.getRank()) {
            case LEADER:
            case COLEADER:
                return "**";
            case CAPTAIN:
                return "*";
            default:
                return "";
        }
    }
}
