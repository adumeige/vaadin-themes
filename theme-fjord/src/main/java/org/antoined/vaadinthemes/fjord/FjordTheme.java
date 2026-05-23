package org.antoined.vaadinthemes.fjord;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class FjordTheme {

    public static final String ID = "fjord";
    public static final String STYLESHEET = "/themes/fjord/styles.css";

    private FjordTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Fjord", STYLESHEET, List.of(
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("light", "Light", ""),
                                ThemeOptionValue.of("polar-night", "Polar Night"),
                                ThemeOptionValue.of("snow-storm", "Snow Storm"),
                                ThemeOptionValue.of("frost", "Frost"),
                                ThemeOptionValue.of("aurora", "Aurora"))
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
