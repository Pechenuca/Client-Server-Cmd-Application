package max.coreSources;

import java.io.Serializable;
import java.util.Random;

public enum OrganizationType implements Serializable {
    COMMERCIAL("Коммерческая"),
    PUBLIC("Открытая"),
    GOVERNMENT("Правительство"),
    TRUST("Трест"),
    PRIVATE_LIMITED_COMPANY("ЗАО");

    private String value;

    OrganizationType(String value) {
        this.value = value;
    }

    public static OrganizationType getRand() {
        int randIndex = new Random().nextInt(values().length);
        return values()[randIndex];
    }
}
