package org.antoined.vaadinthemes.brutalist;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class BrutalistTheme {

    public static final String ID = "brutalist";
    public static final String STYLESHEET = "/themes/brutalist/styles.css";

    private BrutalistTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Brutalist", STYLESHEET, List.of(
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("paper", "Paper", ""),
                                ThemeOptionValue.of("moss", "Moss"),
                                ThemeOptionValue.of("brick", "Brick"),
                                ThemeOptionValue.of("blueprint", "Blueprint"))
                        .build(),
                BasicThemeOption.builder("mode", "Mode")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("light", "Light", ""),
                                ThemeOptionValue.of("dark", "Dark"))
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
