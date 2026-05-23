package org.antoined.vaadinthemes.theme;

@FunctionalInterface
public interface ThemeOptionAction {

    void apply(ThemeOptionContext context, ThemeOption option, String value);
}
