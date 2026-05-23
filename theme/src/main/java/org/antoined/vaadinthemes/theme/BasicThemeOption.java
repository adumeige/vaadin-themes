package org.antoined.vaadinthemes.theme;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record BasicThemeOption(
        String key,
        String label,
        ThemeOptionControl control,
        ThemeOptionTarget target,
        Optional<String> targetName,
        List<ThemeOptionValue> values,
        Optional<String> storageKey,
        boolean persistent,
        Optional<ThemeOptionAction> action
) implements ThemeOption {

    public BasicThemeOption {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(control, "control must not be null");
        Objects.requireNonNull(target, "target must not be null");
        targetName = Objects.requireNonNull(targetName, "targetName must not be null");
        values = List.copyOf(Objects.requireNonNull(values, "values must not be null"));
        storageKey = Objects.requireNonNull(storageKey, "storageKey must not be null");
        action = Objects.requireNonNull(action, "action must not be null");
    }

    public static Builder builder(String key, String label) {
        return new Builder(key, label);
    }

    public static final class Builder {

        private final String key;
        private final String label;
        private ThemeOptionControl control = ThemeOptionControl.SELECT;
        private ThemeOptionTarget target = ThemeOptionTarget.ROOT_DATA_ATTRIBUTE;
        private String targetName;
        private List<ThemeOptionValue> values = List.of();
        private String storageKey;
        private boolean persistent;
        private ThemeOptionAction action;

        private Builder(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public Builder control(ThemeOptionControl control) {
            this.control = control;
            return this;
        }

        public Builder target(ThemeOptionTarget target) {
            this.target = target;
            return this;
        }

        public Builder targetName(String targetName) {
            this.targetName = targetName;
            return this;
        }

        public Builder values(List<ThemeOptionValue> values) {
            this.values = values;
            return this;
        }

        public Builder values(ThemeOptionValue... values) {
            this.values = List.of(values);
            return this;
        }

        public Builder storageKey(String storageKey) {
            this.storageKey = storageKey;
            return this;
        }

        public Builder persistent(boolean persistent) {
            this.persistent = persistent;
            return this;
        }

        public Builder action(ThemeOptionAction action) {
            this.action = action;
            return this;
        }

        public BasicThemeOption build() {
            return new BasicThemeOption(
                    key,
                    label,
                    control,
                    target,
                    Optional.ofNullable(targetName).filter(value -> !value.isBlank()),
                    values,
                    Optional.ofNullable(storageKey).filter(value -> !value.isBlank()),
                    persistent,
                    Optional.ofNullable(action));
        }
    }
}
