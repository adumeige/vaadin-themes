package org.antoined.vaadinthemes.seagod;

import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeDefinition;

import java.util.List;

public final class SeagodTheme {

    public static final String ID = "seagod";
    public static final String STYLESHEET = "/themes/seagod/styles.css";

    private SeagodTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Seagod", STYLESHEET, List.of());
    }
}
