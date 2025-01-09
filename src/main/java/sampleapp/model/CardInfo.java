package sampleapp.model;

import java.util.HashMap;
import java.util.Map;

public enum CardInfo {
    WATER_GOBLIN("WaterGoblin", "monster", "water"),
    FIRE_GOBLIN("FireGoblin", "monster", "fire"),
    REGULAR_GOBLIN("RegularGoblin", "monster", "regular"),
    WATER_TROLL("WaterTroll", "monster", "water"),
    FIRE_TROLL("FireTroll", "monster", "fire"),
    REGULAR_TROLL("RegularTroll", "monster", "regular"),
    WATER_ELF("WaterElf", "monster", "water"),
    FIRE_ELF("FireElf", "monster", "fire"),
    REGULAR_ELF("RegularElf", "monster", "regular"),
    WATER_SPELL("WaterSpell", "monster", "water"),
    FIRE_SPELL("FireSpell", "monster", "fire"),
    REGULAR_SPELL("RegularSpell", "monster", "regular"),
    KNIGHT("Knight", "monster", "regular"),
    DRAGON("Dragon", "monster", "fire"),
    ORK("Ork", "monster", "regular"),
    KRAKEN("Kraken", "monster", "water"),
    WIZARD("Wizzard", "monster", "regular");

    private final String displayName;
    private final String type;
    private final String element;

    private static final Map<String, CardInfo> DISPLAY_NAME_MAP = new HashMap<>();

    static {
        for (CardInfo cardName : values()) {
            DISPLAY_NAME_MAP.put(cardName.displayName, cardName);
        }
    }

    CardInfo(String displayName, String type, String element) {
        this.displayName = displayName;
        this.type = type;
        this.element = element;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CardInfo fromDisplayName(String displayName) {
        return DISPLAY_NAME_MAP.get(displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public String getElement() {
        return element;
    }
}
