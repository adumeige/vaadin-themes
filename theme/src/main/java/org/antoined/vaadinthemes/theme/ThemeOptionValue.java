package org.antoined.vaadinthemes.theme;

import java.util.Objects;

public record ThemeOptionValue(String id, String label, String appliedValue) {

    public ThemeOptionValue {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(label, "label must not be null");
        appliedValue = appliedValue == null ? id : appliedValue;
    }

    public static ThemeOptionValue of(String id, String label) {
        return new ThemeOptionValue(id, label, id);
    }

    public static ThemeOptionValue of(String id, String label, String appliedValue) {
        return new ThemeOptionValue(id, label, appliedValue);
    }
}
