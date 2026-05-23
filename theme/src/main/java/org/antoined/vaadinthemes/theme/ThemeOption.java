package org.antoined.vaadinthemes.theme;

import java.util.List;
import java.util.Optional;

public interface ThemeOption {

    String key();

    String label();

    ThemeOptionControl control();

    ThemeOptionTarget target();

    Optional<String> targetName();

    List<ThemeOptionValue> values();

    Optional<String> storageKey();

    boolean persistent();

    Optional<ThemeOptionAction> action();

    default String effectiveTargetName() {
        return targetName().filter(value -> !value.isBlank()).orElse(key());
    }
}
