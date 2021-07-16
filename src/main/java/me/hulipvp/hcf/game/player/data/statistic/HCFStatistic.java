package me.hulipvp.hcf.game.player.data.statistic;

import lombok.Getter;
import lombok.Setter;

public class HCFStatistic {
    
    @Getter private final StatisticType type;
    @Getter @Setter private int value;

    public HCFStatistic(StatisticType type, int value) {
        this.type = type;
        this.value = value;
    }

    public HCFStatistic increment() {
        this.value++;
        return this;
    }

    public HCFStatistic reduce() {
        this.value--;
        return this;
    }
}
