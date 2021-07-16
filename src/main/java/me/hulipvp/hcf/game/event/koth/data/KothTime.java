package me.hulipvp.hcf.game.event.koth.data;

import lombok.Getter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public enum KothTime {

    ONE(1),
    FIVE(5),
    TEN(10),
    FIFTEEN(15),
    TWENTY(20),
    TWENTY_FIVE(25),
    THIRTY(30);

    @Getter private final long minutes;

    KothTime(long minutes) {
        this.minutes = minutes;
    }

    public long toMills() {
        return TimeUnit.MINUTES.toMillis(this.getMinutes());
    }

    public static KothTime getByMinutes(int minutes) {
        return Arrays.stream(values())
                .filter(time -> time.getMinutes() == minutes)
                .findFirst()
                .orElse(FIFTEEN);
    }
}
