package me.hulipvp.hcf.game.player.data;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.faction.ClaimPillar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ClaimData {

    @Getter @Setter private UUID currentTerritory, claimingFaction;
    @Getter @Setter private boolean claiming;
    @Getter @Setter private Location pos1, pos2;
    @Getter @Setter private Long lastClaim;
    @Getter @Setter private List<ClaimPillar> pillars;

    public void updatePillars(Player claimer) {
        if(this.getPos1() == null || this.getPos2() == null)
            return;

        List<Location> locs = Arrays.asList(
                this.getPos1(),
                this.getPos2(),
                new Location(this.getPos1().getWorld(), this.getPos1().getBlockX(), 0, this.getPos2().getBlockZ()),
                new Location(this.getPos1().getWorld(), this.getPos2().getBlockX(), 0, this.getPos1().getBlockZ())
        );

        Iterator<ClaimPillar> pillarIterator = this.getPillars().iterator();
        while(pillarIterator.hasNext()) {
            ClaimPillar pillar = pillarIterator.next();
            boolean foundSame = locs.stream()
                    .anyMatch(location -> {
                        return isSameLoc(pillar.getLocation(), location);
                    });

            if(!foundSame) {
                pillar.remove(claimer);
                pillarIterator.remove();
            }
        }

        locs.forEach(location -> {
            this.getPillars().add(new ClaimPillar(location, Material.EMERALD_BLOCK));
        });

        this.getPillars().forEach(pillar -> pillar.display(claimer));
    }

    public void removePillars(Player claimer) {
        if(this.getPillars() != null && !this.getPillars().isEmpty()) {
            this.getPillars().forEach(pillar -> pillar.remove(claimer));
            this.getPillars().clear();
        }
    }

    private boolean isSameLoc(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockZ() == loc2.getBlockZ();
    }
}
