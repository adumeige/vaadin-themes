package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.shared.ThemeVariant;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record BasicThemeDefinition(
        String id,
        String label,
        Optional<String> stylesheetPath,
        List<ThemeOption> options,
        List<ThemeVariant> variants
) implements ThemeDefinition {

    public BasicThemeDefinition {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(label, "label must not be null");
        stylesheetPath = Objects.requireNonNull(stylesheetPath, "stylesheetPath must not be null");
        options = List.copyOf(Objects.requireNonNull(options, "options must not be null"));
        variants = List.copyOf(Objects.requireNonNull(variants, "variants must not be null"));
    }

    public BasicThemeDefinition(String id, String label, String stylesheetPath, List<ThemeOption> options) {
        this(id, label, Optional.ofNullable(stylesheetPath).filter(path -> !path.isBlank()), options, List.of());
    }

    public BasicThemeDefinition(
            String id,
            String label,
            String stylesheetPath,
            List<ThemeOption> options,
            List<ThemeVariant> variants
    ) {
        this(id, label, Optional.ofNullable(stylesheetPath).filter(path -> !path.isBlank()), options, variants);
    }

    public static BasicThemeDefinition of(String id, String label, String stylesheetPath, List<ThemeOption> options) {
        return new BasicThemeDefinition(id, label, stylesheetPath, options);
    }

    public static BasicThemeDefinition of(
            String id,
            String label,
            String stylesheetPath,
            List<ThemeOption> options,
            List<ThemeVariant> variants
    ) {
        return new BasicThemeDefinition(id, label, stylesheetPath, options, variants);
    }
}
