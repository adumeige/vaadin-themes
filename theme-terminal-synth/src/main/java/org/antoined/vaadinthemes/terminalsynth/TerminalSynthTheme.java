package org.antoined.vaadinthemes.terminalsynth;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.List;

public final class TerminalSynthTheme {

    public static final String ID = "terminal-synth";
    public static final String STYLESHEET = "/themes/terminal-synth/styles.css";

    private TerminalSynthTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Terminal Synth", STYLESHEET, List.of(
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("outrun", "Outrun"),
                                ThemeOptionValue.of("miami", "Miami"),
                                ThemeOptionValue.of("tokyo", "Tokyo"),
                                ThemeOptionValue.of("dusk", "Dusk"))
                        .build(),
                BasicThemeOption.builder("density", "Density")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("compact", "Compact"),
                                ThemeOptionValue.of("default", "Default", ""),
                                ThemeOptionValue.of("comfortable", "Comfortable"))
                        .build(),
                BasicThemeOption.builder("scanlines", "CRT")
                        .control(ThemeOptionControl.TOGGLE)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("on", "On", "on"),
                                ThemeOptionValue.of("off", "Off", ""))
                        .build()));
    }
}
