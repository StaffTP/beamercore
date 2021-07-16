package me.hulipvp.hcf.backend;

import lombok.Getter;

import java.util.Arrays;

public enum BackendType {

    REDIS("Redis"),
    MONGO("Mongo"),
    FLAT_FILE("Flat_file");

    @Getter private static BackendType fallback = BackendType.MONGO;

    @Getter private String verboseName;

    BackendType(String verboseName) {
        this.verboseName = verboseName;
    }

    public static BackendType getOrDefault(String match) {
        if(match == null)
            return fallback;

        return Arrays.stream(values())
                .filter(type -> type.getVerboseName().equalsIgnoreCase(match))
                .findFirst()
                .orElse(fallback);
    }
}
