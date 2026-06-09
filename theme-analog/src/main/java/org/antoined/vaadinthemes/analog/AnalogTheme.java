package org.antoined.vaadinthemes.analog;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class AnalogTheme {

    public static final String ID = "analog";
    public static final String STYLESHEET = "/themes/analog/styles.css";

    private AnalogTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Analog", STYLESHEET, List.of(
                BasicThemeOption.builder("finish", "Finish")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("graphite", "Graphite", ""),
                                ThemeOptionValue.of("aqua", "Aqua"))
                        .build(),
                BasicThemeOption.builder("glow", "Glow")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("amber", "Amber", ""),
                                ThemeOptionValue.of("green", "VFD Green"),
                                ThemeOptionValue.of("cyan", "Ice Cyan"),
                                ThemeOptionValue.of("red", "Alert Red"))
                        .build()));
    }
}
