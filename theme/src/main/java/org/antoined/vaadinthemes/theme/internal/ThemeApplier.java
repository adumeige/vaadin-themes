package org.antoined.vaadinthemes.theme.internal;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.dom.ThemeList;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOption;
import org.antoined.vaadinthemes.theme.ThemeOptionContext;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class ThemeApplier {

    private static final String STYLESHEET_LINK_ID = "vaadin-theme-switcher-css";

    public void applyTheme(
            ThemeDefinition theme,
            Collection<ThemeDefinition> knownThemes,
            AppLayout appLayout,
            boolean persistSelection,
            String selectionStorageKey
    ) {
        Objects.requireNonNull(theme, "theme must not be null");
        UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }

        if (appLayout != null) {
            appLayout.getElement().removeAttribute("theme");
        }

        String stylesheetPath = theme.stylesheetPath().orElse("");
        ui.getPage().executeJs(
                clearRootStateScript(knownThemes) +
                        "const existing = document.getElementById($0);" +
                        "if (existing) existing.remove();" +
                        "if ($1) {" +
                        "  const link = document.createElement('link');" +
                        "  link.id = $0;" +
                        "  link.rel = 'stylesheet';" +
                        "  link.href = $1;" +
                        "  document.head.appendChild(link);" +
                        "}",
                STYLESHEET_LINK_ID,
                stylesheetPath);

        if (persistSelection) {
            ui.getPage().executeJs("localStorage.setItem($0, $1)", selectionStorageKey, theme.id());
        }
    }

    public void applyOption(ThemeOption option, String value, AppLayout appLayout) {
        Objects.requireNonNull(option, "option must not be null");
        UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }

        if (option.target() == ThemeOptionTarget.CUSTOM_ACTION) {
            option.action().ifPresent(action -> action.apply(new ThemeOptionContext(ui, appLayout), option, value));
            return;
        }

        String appliedValue = appliedValue(option, value);
        switch (option.target()) {
            case ROOT_THEME_TOKEN -> applyRootThemeToken(ui, option, appliedValue);
            case ROOT_DATA_ATTRIBUTE -> applyRootDataAttribute(ui, option, appliedValue);
            case ROOT_CSS_VARIABLE -> applyRootCssVariable(ui, option, appliedValue);
            case APP_LAYOUT_THEME_TOKEN -> applyAppLayoutThemeToken(appLayout, option, appliedValue);
            case CUSTOM_ACTION -> throw new IllegalStateException("CUSTOM_ACTION should be handled before switch");
        }
    }

    public String storageKey(ThemeDefinition theme, ThemeOption option) {
        return option.storageKey().orElse("vaadin-theme:" + theme.id() + ":" + option.key());
    }

    private void applyRootThemeToken(UI ui, ThemeOption option, String value) {
        ui.getPage().executeJs(
                "const h = document.documentElement;" +
                        "const remove = $0.split(' ').filter(Boolean);" +
                        "const add = ($1 || '').split(' ').filter(Boolean);" +
                        "const tokens = (h.getAttribute('theme') || '').split(/\\s+/).filter(Boolean)" +
                        "  .filter(token => !remove.includes(token));" +
                        "add.forEach(token => { if (!tokens.includes(token)) tokens.push(token); });" +
                        "if (tokens.length) h.setAttribute('theme', tokens.join(' '));" +
                        "else h.removeAttribute('theme');",
                String.join(" ", knownAppliedTokens(option)),
                value == null ? "" : value);
    }

    private void applyRootDataAttribute(UI ui, ThemeOption option, String value) {
        String attribute = "data-" + option.effectiveTargetName();
        if (value == null || value.isBlank()) {
            ui.getPage().executeJs("document.documentElement.removeAttribute($0)", attribute);
        } else {
            ui.getPage().executeJs("document.documentElement.setAttribute($0, $1)", attribute, value);
        }
    }

    private void applyRootCssVariable(UI ui, ThemeOption option, String value) {
        String property = option.effectiveTargetName();
        if (value == null || value.isBlank()) {
            ui.getPage().executeJs("document.documentElement.style.removeProperty($0)", property);
        } else {
            ui.getPage().executeJs("document.documentElement.style.setProperty($0, $1)", property, value);
        }
    }

    private void applyAppLayoutThemeToken(AppLayout appLayout, ThemeOption option, String value) {
        if (appLayout == null) {
            return;
        }
        ThemeList themeList = appLayout.getElement().getThemeList();
        knownAppliedTokens(option).forEach(themeList::remove);
        splitTokens(value).forEach(themeList::add);
    }

    private String appliedValue(ThemeOption option, String value) {
        if (value == null) {
            return null;
        }
        return option.values().stream()
                .filter(optionValue -> optionValue.id().equals(value))
                .findFirst()
                .map(ThemeOptionValue::appliedValue)
                .orElse(value);
    }

    private Set<String> knownAppliedTokens(ThemeOption option) {
        Set<String> tokens = new LinkedHashSet<>();
        option.values().stream()
                .map(ThemeOptionValue::appliedValue)
                .flatMap(value -> splitTokens(value).stream())
                .forEach(tokens::add);
        return tokens;
    }

    private String clearRootStateScript(Collection<ThemeDefinition> knownThemes) {
        Set<String> dataAttributes = new LinkedHashSet<>();
        Set<String> cssVariables = new LinkedHashSet<>();

        for (ThemeDefinition theme : knownThemes) {
            for (ThemeOption option : theme.options()) {
                if (option.target() == ThemeOptionTarget.ROOT_DATA_ATTRIBUTE) {
                    dataAttributes.add("data-" + option.effectiveTargetName());
                } else if (option.target() == ThemeOptionTarget.ROOT_CSS_VARIABLE) {
                    cssVariables.add(option.effectiveTargetName());
                }
            }
        }

        StringBuilder script = new StringBuilder("const h = document.documentElement;h.removeAttribute('theme');");
        dataAttributes.forEach(attribute -> script.append("h.removeAttribute(").append(jsString(attribute)).append(");"));
        cssVariables.forEach(variable -> script.append("h.style.removeProperty(").append(jsString(variable)).append(");"));
        return script.toString();
    }

    private static Set<String> splitTokens(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return new LinkedHashSet<>(Arrays.asList(value.trim().split("\\s+")));
    }

    private static String jsString(String value) {
        StringBuilder out = new StringBuilder("'");
        for (char c : value.toCharArray()) {
            if (c == '\'' || c == '\\') {
                out.append('\\');
            }
            out.append(c);
        }
        out.append('\'');
        return out.toString();
    }
}
