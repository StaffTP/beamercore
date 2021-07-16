package me.hulipvp.hcf.game.event.dtc;

import lombok.Getter;
import lombok.Setter;
import me.hulipvp.hcf.game.event.dtc.data.DTCEndReason;
import me.hulipvp.hcf.game.faction.type.event.DTCFaction;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.api.events.dtc.DTCEndEvent;
import me.hulipvp.hcf.api.events.dtc.DTCStartEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DTC {

    @Getter private static final Map<String, DTC> DTCs = new HashMap<>();
    @Getter @Setter private static DTC activeDTC;

    @Getter private final String name;
    @Getter @Setter private DTCFaction faction;
    @Getter private final Map<String, Integer> points;
    @Getter @Setter private UUID finalCapper;

    public DTC(String name) {
        this.name = name;
        this.faction = null;
        this.points = new HashMap<>();
        this.finalCapper = null;
    }

    public void start() {
        setActiveDTC(this);

        DTCStartEvent event = new DTCStartEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void stop() {
        if(getActiveDTC() == this) {
            setActiveDTC(null);

            DTCEndEvent event = new DTCEndEvent(this, DTCEndReason.CANCELLED);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    public void win() {
        if(getActiveDTC() == this) {
            setActiveDTC(null);

            DTCEndEvent event = new DTCEndEvent(this, DTCEndReason.WON);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    public void save() {
        HCF.getInstance().getBackend().saveDTC(this);
    }

    public static DTC getDTC(String name) {
        return getDTCs().get(name);
    }

    public Document toDocument() {
        return new Document()
                .append("name", this.getName())
                .append("faction", (this.getFaction() == null) ? null : this.getFaction().getUuid().toString());
    }
}