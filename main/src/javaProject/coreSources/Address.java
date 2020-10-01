package javaProject.coreSources;

import java.util.Random;

public enum Address {
    CITY,
    TOWN,
    VILLAGE;

    @Override
    public String toString() {
        return this.name();
    }

    public static Address getRand() {
        int randIndex = new Random().nextInt(values().length);
        return values()[randIndex];
    }
}