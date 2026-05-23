package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.ComponentEvent;

public final class ThemeChangeEvent extends ComponentEvent<ThemeSwitcher> {

    private final ThemeDefinition theme;

    public ThemeChangeEvent(ThemeSwitcher source, boolean fromClient, ThemeDefinition theme) {
        super(source, fromClient);
        this.theme = theme;
    }

    public ThemeDefinition getTheme() {
        return theme;
    }
}
