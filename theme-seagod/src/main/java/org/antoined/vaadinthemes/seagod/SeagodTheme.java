package org.antoined.vaadinthemes.seagod;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class SeagodTheme {

    public static final String ID = "seagod";
    public static final String STYLESHEET = "/themes/seagod/styles.css";

    private SeagodTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Seagod", STYLESHEET, List.of(
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("poseidon", "Poseidon", ""),
                                ThemeOptionValue.of("lagoon", "Lagoon"),
                                ThemeOptionValue.of("coral-reef", "Coral Reef"),
                                ThemeOptionValue.of("abyss", "Abyss"))
                        .build(),
                BasicThemeOption.builder("density", "Density")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("comfortable", "Comfortable"),
                                ThemeOptionValue.of("regular", "Regular", ""),
                                ThemeOptionValue.of("compact", "Compact"))
                        .build()));
    }
}
