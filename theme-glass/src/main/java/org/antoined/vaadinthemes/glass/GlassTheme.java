package org.antoined.vaadinthemes.glass;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.shared.ThemeVariant;
import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;
import org.antoined.vaadinthemes.theme.ThemeVariants;

import java.util.List;

public final class GlassTheme {

    public static final String ID = "glass";
    public static final String STYLESHEET = "/themes/glass/styles.css";

    private GlassTheme() {
    }

    public static ThemeDefinition definition() {
        return BasicThemeDefinition.of(ID, "Glass", STYLESHEET, List.of(
                BasicThemeOption.builder("mode", "Mode")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_THEME_TOKEN)
                        .values(
                                ThemeOptionValue.of("light", "Light", "light"),
                                ThemeOptionValue.of("dark", "Dark", "dark"))
                        .build(),
                BasicThemeOption.builder("layout", "Shell layout")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.APP_LAYOUT_THEME_TOKEN)
                        .values(
                                ThemeOptionValue.of("rounded", "Rounded", ""),
                                ThemeOptionValue.of("flat", "Flat", "flat-layout"))
                        .build(),
                BasicThemeOption.builder("glass", "Glass")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("subtle", "Subtle"),
                                ThemeOptionValue.of("standard", "Standard", ""),
                                ThemeOptionValue.of("heavy", "Heavy"))
                        .build(),
                BasicThemeOption.builder("backgroundMode", "Background")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .targetName("background-mode")
                        .values(
                                ThemeOptionValue.of("aurora", "Aurora", ""),
                                ThemeOptionValue.of("flat-color", "Flat color"))
                        .build(),
                color("bg", "Background", "--vg-bg"),
                color("primary", "Primary", "--vg-primary"),
                color("secondary", "Secondary", "--vg-secondary"),
                color("accent", "Accent", "--vg-accent")), List.of(GlassThemeVariant.values()));
    }

    public static List<ThemeVariant> variants() {
        return List.of(GlassThemeVariant.values());
    }

    public static void applyVariant(Component component, GlassThemeVariant variant) {
        ThemeVariants.replace(component, variant, GlassThemeVariant.class);
    }

    private static BasicThemeOption color(String key, String label, String cssVariable) {
        return BasicThemeOption.builder(key, label)
                .control(ThemeOptionControl.COLOR)
                .target(ThemeOptionTarget.ROOT_CSS_VARIABLE)
                .targetName(cssVariable)
                .persistent(true)
                .build();
    }
}
