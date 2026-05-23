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
                BasicThemeOption.builder("density", "Density")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_DATA_ATTRIBUTE)
                        .values(
                                ThemeOptionValue.of("comfortable", "Comfortable"),
                                ThemeOptionValue.of("regular", "Regular", ""),
                                ThemeOptionValue.of("compact", "Compact"))
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
                BasicThemeOption.builder("palette", "Palette")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.CUSTOM_ACTION)
                        .values(
                                ThemeOptionValue.of("crystal", "Crystal"),
                                ThemeOptionValue.of("glacier", "Glacier"),
                                ThemeOptionValue.of("aurora", "Aurora"),
                                ThemeOptionValue.of("prism", "Prism"),
                                ThemeOptionValue.of("ember", "Ember"),
                                ThemeOptionValue.of("opal", "Opal"),
                                ThemeOptionValue.of("lagoon", "Lagoon"),
                                ThemeOptionValue.of("sakura", "Sakura"),
                                ThemeOptionValue.of("graphite", "Graphite"),
                                ThemeOptionValue.of("twilight", "Twilight"))
                        .persistent(true)
                        .action((context, option, value) -> applyPalettePreset(context, value))
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

    private static void applyPalettePreset(org.antoined.vaadinthemes.theme.ThemeOptionContext context, String value) {
        GlassPalettePreset preset = GlassPalettePreset.from(value);
        context.ui().getPage().executeJs("""
                const h = document.documentElement;
                const colors = {
                  bg: $0,
                  primary: $1,
                  secondary: $2,
                  accent: $3
                };
                const labels = {
                  bg: 'Background',
                  primary: 'Primary',
                  secondary: 'Secondary',
                  accent: 'Accent'
                };
                h.setAttribute('data-glass-palette', $4);
                Object.entries(colors).forEach(([key, color]) => {
                  h.style.setProperty('--vg-' + key, color);
                  localStorage.removeItem('vaadin-theme:glass:' + key);
                  document.querySelectorAll('input.theme-switcher-color-input').forEach(input => {
                    if (input.title === labels[key]) {
                      input.value = color;
                      input.setAttribute('value', color);
                    }
                  });
                });
                """,
                preset.bg(),
                preset.primary(),
                preset.secondary(),
                preset.accent(),
                preset.id());
    }

    private record GlassPalettePreset(String id, String bg, String primary, String secondary, String accent) {

        static GlassPalettePreset from(String value) {
            return switch (value == null ? "" : value) {
                case "glacier" -> new GlassPalettePreset("glacier", "#e6f6ff", "#0284c7", "#64748b", "#14b8a6");
                case "aurora" -> new GlassPalettePreset("aurora", "#f7e9f3", "#c026d3", "#06b6d4", "#f97316");
                case "prism" -> new GlassPalettePreset("prism", "#f3f0ff", "#7c3aed", "#0f766e", "#f43f5e");
                case "ember" -> new GlassPalettePreset("ember", "#fff1e8", "#ea580c", "#7c2d12", "#facc15");
                case "opal" -> new GlassPalettePreset("opal", "#f5fbff", "#2563eb", "#0891b2", "#a855f7");
                case "lagoon" -> new GlassPalettePreset("lagoon", "#e6fffb", "#0f766e", "#0284c7", "#84cc16");
                case "sakura" -> new GlassPalettePreset("sakura", "#fff1f5", "#e11d48", "#9333ea", "#f59e0b");
                case "graphite" -> new GlassPalettePreset("graphite", "#e5e7eb", "#334155", "#64748b", "#22c55e");
                case "twilight" -> new GlassPalettePreset("twilight", "#eef2ff", "#4f46e5", "#7c3aed", "#ec4899");
                default -> new GlassPalettePreset("crystal", "#e9eef5", "#4763e4", "#64748b", "#f59e0b");
            };
        }
    }
}
