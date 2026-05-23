package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Optional;

public final class ThemeSwitcherPopover extends Composite<Span> {

    private final Span root = new Span();
    private final Button trigger = new Button("Theme", VaadinIcon.PALETTE.create());
    private final ThemeSwitcher switcher;
    private final Popover popover;

    public ThemeSwitcherPopover(List<ThemeDefinition> themes) {
        this(themes, null);
    }

    public ThemeSwitcherPopover(List<ThemeDefinition> themes, AppLayout appLayout) {
        switcher = new ThemeSwitcher(themes, appLayout);
        switcher.setPresentation(ThemeSwitcher.Presentation.PANEL);

        popover = new Popover(switcher);
        popover.setTarget(trigger);
        popover.setPosition(PopoverPosition.BOTTOM_END);
        popover.setOpenOnClick(true);
        popover.setCloseOnEsc(true);
        popover.setCloseOnOutsideClick(true);
        popover.setAriaLabel("Theme settings");
        popover.setClassName("theme-switcher-popover-overlay");

        root.addClassName("theme-switcher-popover");
        trigger.addClassName("theme-switcher-popover-trigger");
        root.add(trigger, popover);
    }

    @Override
    protected Span initContent() {
        return root;
    }

    public ThemeSwitcher getThemeSwitcher() {
        return switcher;
    }

    public Button getTrigger() {
        return trigger;
    }

    public Popover getPopover() {
        return popover;
    }

    public void setSelectedTheme(String themeId) {
        switcher.setSelectedTheme(themeId);
    }

    public Optional<ThemeDefinition> getSelectedTheme() {
        return switcher.getSelectedTheme();
    }

    public boolean isPersistenceEnabled() {
        return switcher.isPersistenceEnabled();
    }

    public void setPersistenceEnabled(boolean persistenceEnabled) {
        switcher.setPersistenceEnabled(persistenceEnabled);
    }

    public String getSelectedThemeStorageKey() {
        return switcher.getSelectedThemeStorageKey();
    }

    public void setSelectedThemeStorageKey(String selectedThemeStorageKey) {
        switcher.setSelectedThemeStorageKey(selectedThemeStorageKey);
    }

    public void setOptionValue(String optionKey, String value) {
        switcher.setOptionValue(optionKey, value);
    }

    public void setOptionValue(String optionKey, String value, boolean persist) {
        switcher.setOptionValue(optionKey, value, persist);
    }

    public Registration addThemeChangeListener(ComponentEventListener<ThemeChangeEvent> listener) {
        return switcher.addThemeChangeListener(listener);
    }
}
