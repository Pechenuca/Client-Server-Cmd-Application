package max.coreSources;

import java.util.Random;

public enum Color {
    RED,
    BLUE,
    YELLOW,
    ORANGE,
    WHITE;

    @Override
    public String toString() {
        return this.name();
    }

    public static Color getRand() {
        int randIndex = new Random().nextInt(values().length);
        return values()[randIndex];
    }
}