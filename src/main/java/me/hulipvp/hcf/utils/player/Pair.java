package me.hulipvp.hcf.utils.player;

import lombok.Getter;

public class Pair<K, V> {

    @Getter
    private K key;

    @Getter
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
