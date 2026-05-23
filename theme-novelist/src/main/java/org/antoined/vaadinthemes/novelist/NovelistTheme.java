package org.antoined.vaadinthemes.novelist;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class NovelistTheme {

    public static final String ID = "novelist";
    public static final String STYLESHEET = "/themes/novelist/styles.css";

    private NovelistTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Novelist", STYLESHEET, List.of(
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("codex", "Codex"),
                                ThemeOptionValue.of("scriptorium", "Scriptorium"),
                                ThemeOptionValue.of("saga", "Saga"))
                        .build(),
                BasicThemeOption.builder("mode", "Mode")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("light", "Light"),
                                ThemeOptionValue.of("dark", "Dark"))
                        .build(),
                BasicThemeOption.builder("density", "Density")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("compact", "Compact"),
                                ThemeOptionValue.of("regular", "Regular", ""),
                                ThemeOptionValue.of("comfortable", "Comfortable"))
                        .build()));
    }
}
