package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.dom.ThemeList;

import java.util.Optional;

public final class ThemeOptionContext {

    private final UI ui;
    private final AppLayout appLayout;

    public ThemeOptionContext(UI ui, AppLayout appLayout) {
        this.ui = ui;
        this.appLayout = appLayout;
    }

    public UI ui() {
        return ui;
    }

    public Optional<AppLayout> appLayout() {
        return Optional.ofNullable(appLayout);
    }

    public void executeJs(String expression, Object... parameters) {
        ui.getPage().executeJs(expression, parameters);
    }

    public void setRootAttribute(String attribute, String value) {
        executeJs("document.documentElement.setAttribute($0, $1)", attribute, value);
    }

    public void removeRootAttribute(String attribute) {
        executeJs("document.documentElement.removeAttribute($0)", attribute);
    }

    public void setRootData(String key, String value) {
        setRootAttribute("data-" + key, value);
    }

    public void removeRootData(String key) {
        removeRootAttribute("data-" + key);
    }

    public void setRootCssVariable(String name, String value) {
        executeJs("document.documentElement.style.setProperty($0, $1)", name, value);
    }

    public void removeRootCssVariable(String name) {
        executeJs("document.documentElement.style.removeProperty($0)", name);
    }

    public void replaceAppLayoutThemeTokens(Iterable<String> knownTokens, String selectedTokens) {
        if (appLayout == null) {
            return;
        }
        ThemeList themeList = appLayout.getElement().getThemeList();
        knownTokens.forEach(themeList::remove);
        splitTokens(selectedTokens).forEach(themeList::add);
    }

    private static Iterable<String> splitTokens(String value) {
        if (value == null || value.isBlank()) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(value.trim().split("\\s+")).toList();
    }
}
