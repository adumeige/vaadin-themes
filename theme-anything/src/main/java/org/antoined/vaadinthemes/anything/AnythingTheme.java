package org.antoined.vaadinthemes.anything;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class AnythingTheme {

    public static final String ID = "anything";
    public static final String STYLESHEET = "/themes/anything/styles.css";

    private AnythingTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Anything", STYLESHEET, List.of(
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("paper", "Paper"),
                                ThemeOptionValue.of("bone", "Bone"),
                                ThemeOptionValue.of("linen", "Linen"),
                                ThemeOptionValue.of("sage", "Sage"),
                                ThemeOptionValue.of("slate", "Slate"))
                        .build(),
                BasicThemeOption.builder("mode", "Mode")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("light", "Light"),
                                ThemeOptionValue.of("dark", "Dark"))
                        .build(),
                BasicThemeOption.builder("accent", "Accent")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("oxblood", "Oxblood"),
                                ThemeOptionValue.of("forest", "Forest"),
                                ThemeOptionValue.of("ink", "Ink blue"),
                                ThemeOptionValue.of("brass", "Brass"),
                                ThemeOptionValue.of("plum", "Plum"),
                                ThemeOptionValue.of("graphite", "Graphite"))
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
