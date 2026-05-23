package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.shared.ThemeVariant;

import java.util.List;
import java.util.Optional;

/**
 * Describes one runtime-selectable theme.
 *
 * <p>Theme modules should provide their own implementation or factory for this
 * interface. Consuming applications decide which definitions to expose and in
 * which order.</p>
 */
public interface ThemeDefinition {

    String id();

    String label();

    Optional<String> stylesheetPath();

    List<ThemeOption> options();

    default List<ThemeVariant> variants() {
        return List.of();
    }
}
