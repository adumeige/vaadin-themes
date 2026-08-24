package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;
import org.antoined.vaadinthemes.theme.internal.ThemeApplier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ThemeSwitcher extends Composite<HorizontalLayout> {

    public enum Presentation {
        TOOLBAR,
        PANEL
    }

    private final HorizontalLayout root = new HorizontalLayout();
    private final ComboBox<ThemeDefinition> themeSelector = new ComboBox<>();
    private final HorizontalLayout optionsBar = new HorizontalLayout();
    private final List<ThemeDefinition> themes;
    private final Map<String, OptionBinding> optionBindings = new LinkedHashMap<>();
    private final ThemeApplier applier = new ThemeApplier();
    private final AppLayout appLayout;

    private ThemeDefinition selectedTheme;
    private String fallbackThemeId;
    private boolean persistenceEnabled;
    private String selectedThemeStorageKey = "vaadin-theme:selected";
    private boolean suppressOptionPersistence;
    private Presentation presentation = Presentation.TOOLBAR;

    public ThemeSwitcher(List<ThemeDefinition> themes) {
        this(themes, null);
    }

    public ThemeSwitcher(List<ThemeDefinition> themes, AppLayout appLayout) {
        this.themes = List.copyOf(Objects.requireNonNull(themes, "themes must not be null"));
        this.appLayout = appLayout;
        configureLayout();
        configureThemeSelector();
        addAttachListener(event -> {
            // Deferred to the end of the round-trip: callers configure the storage key,
            // persistence and the fallback theme right after adding the switcher, so
            // reading those fields during the attach event itself would see stale values.
            event.getUI().beforeClientResponse(this, context -> {
                if (!isAttached()) {
                    return;
                }
                if (persistenceEnabled) {
                    restoreSelectedThemeOrApplyCurrent(event.getUI());
                } else if (selectedTheme != null) {
                    setSelectedTheme(selectedTheme, false);
                }
            });
        });
    }

    @Override
    protected HorizontalLayout initContent() {
        return root;
    }

    public void setSelectedTheme(String themeId) {
        if (themeId == null || themeId.isBlank()) {
            return;
        }
        findTheme(themeId).ifPresent(theme -> setSelectedTheme(theme, false));
    }

    /**
     * Theme applied on attach when nothing has been selected yet and persistence holds no
     * selection. Unlike {@link #setSelectedTheme(String)} this never applies or persists a
     * theme by itself, so it is safe to call on a switcher that is already attached.
     */
    public void setFallbackThemeId(String fallbackThemeId) {
        this.fallbackThemeId = fallbackThemeId;
    }

    public String getFallbackThemeId() {
        return fallbackThemeId;
    }

    public Optional<ThemeDefinition> getSelectedTheme() {
        return Optional.ofNullable(selectedTheme);
    }

    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    public void setPersistenceEnabled(boolean persistenceEnabled) {
        this.persistenceEnabled = persistenceEnabled;
    }

    public String getSelectedThemeStorageKey() {
        return selectedThemeStorageKey;
    }

    public void setSelectedThemeStorageKey(String selectedThemeStorageKey) {
        if (selectedThemeStorageKey == null || selectedThemeStorageKey.isBlank()) {
            throw new IllegalArgumentException("selectedThemeStorageKey must not be blank");
        }
        this.selectedThemeStorageKey = selectedThemeStorageKey;
    }

    public ComboBox<ThemeDefinition> getThemeSelector() {
        return themeSelector;
    }

    public HorizontalLayout getOptionsBar() {
        return optionsBar;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = Objects.requireNonNull(presentation, "presentation must not be null");
        configurePresentation();
        if (selectedTheme != null) {
            rebuildOptions(selectedTheme);
        }
    }

    public void setOptionValue(String optionKey, String value) {
        setOptionValue(optionKey, value, true);
    }

    public void setOptionValue(String optionKey, String value, boolean persist) {
        OptionBinding binding = optionBindings.get(optionKey);
        if (binding != null) {
            boolean previousSuppressOptionPersistence = suppressOptionPersistence;
            suppressOptionPersistence = !persist;
            try {
                binding.setStoredValue(value);
            } finally {
                suppressOptionPersistence = previousSuppressOptionPersistence;
            }
        }
    }

    public Registration addThemeChangeListener(ComponentEventListener<ThemeChangeEvent> listener) {
        return addListener(ThemeChangeEvent.class, listener);
    }

    private void configureLayout() {
        root.addClassName("theme-switcher");
        themeSelector.addClassName("theme-switcher-theme-selector");
        optionsBar.addClassName("theme-switcher-options");
        root.setAlignItems(FlexComponent.Alignment.CENTER);
        root.setSpacing(true);
        optionsBar.setAlignItems(FlexComponent.Alignment.CENTER);
        optionsBar.setSpacing(true);
        root.add(themeSelector, optionsBar);
        configurePresentation();
    }

    private void configurePresentation() {
        root.removeClassName("theme-switcher--toolbar");
        root.removeClassName("theme-switcher--panel");
        root.addClassName(presentation == Presentation.PANEL ? "theme-switcher--panel" : "theme-switcher--toolbar");
    }

    private void configureThemeSelector() {
        themeSelector.setItems(themes);
        themeSelector.setItemLabelGenerator(ThemeDefinition::label);
        themeSelector.setPlaceholder("Theme...");
        themeSelector.setClearButtonVisible(false);
        themeSelector.addValueChangeListener(event -> {
            if (event.isFromClient() && event.getValue() != null) {
                setSelectedTheme(event.getValue(), true);
            }
        });
    }

    private void setSelectedTheme(ThemeDefinition theme, boolean fromClient) {
        selectedTheme = theme;
        if (themeSelector.getValue() != theme) {
            themeSelector.setValue(theme);
        }
        if (!isAttached()) {
            // Applying a theme mutates the live document through executeJs. A switcher that
            // is not attached (built in a constructor, or sitting in an unselected TabSheet
            // tab) must not clobber the theme the user is currently looking at: only record
            // the selection here, the attach listener applies it for real.
            return;
        }
        applier.applyTheme(theme, themes, appLayout, persistenceEnabled, selectedThemeStorageKey);
        rebuildOptions(theme);
        fireEvent(new ThemeChangeEvent(this, fromClient, theme));
    }

    private void rebuildOptions(ThemeDefinition theme) {
        optionBindings.clear();
        optionsBar.removeAll();
        for (ThemeOption option : theme.options()) {
            OptionBinding binding = createOptionBinding(theme, option);
            optionBindings.put(option.key(), binding);
            optionsBar.add(renderOption(option, binding.component()));
            if (persistenceEnabled && option.persistent() && isAttached()) {
                restoreOptionValue(theme, option, binding);
            }
        }
    }

    private Component renderOption(ThemeOption option, Component component) {
        if (presentation == Presentation.TOOLBAR) {
            return component;
        }

        Span label = new Span(option.label());
        label.addClassName("theme-switcher-option-label");

        HorizontalLayout row = new HorizontalLayout(label, component);
        row.addClassName("theme-switcher-option");
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        return row;
    }

    private OptionBinding createOptionBinding(ThemeDefinition theme, ThemeOption option) {
        return switch (option.control()) {
            case SELECT -> createSelectBinding(theme, option);
            case TOGGLE -> createToggleBinding(theme, option);
            case COLOR -> createColorBinding(theme, option);
            case ACTION -> createActionBinding(theme, option);
        };
    }

    private OptionBinding createSelectBinding(ThemeDefinition theme, ThemeOption option) {
        Select<ThemeOptionValue> select = new Select<>();
        select.addClassName("theme-switcher-option-control");
        select.setItems(option.values());
        select.setItemLabelGenerator(ThemeOptionValue::label);
        select.setPlaceholder(option.label());
        select.setTooltipText(option.label());
        select.setWidth("130px");
        select.addValueChangeListener(event -> {
            String value = event.getValue() == null ? null : event.getValue().id();
            applyOption(theme, option, value);
        });
        return new OptionBinding(select, storedValue -> {
            Optional<ThemeOptionValue> matchedValue = option.values().stream()
                    .filter(value -> value.id().equals(storedValue))
                    .findFirst();
            matchedValue.ifPresent(value -> {
                select.setValue(value);
                applyOption(theme, option, storedValue);
            });
        });
    }

    private OptionBinding createToggleBinding(ThemeDefinition theme, ThemeOption option) {
        Checkbox checkbox = new Checkbox(option.label());
        checkbox.addClassName("theme-switcher-option-control");
        checkbox.addValueChangeListener(event -> {
            String value = Boolean.TRUE.equals(event.getValue()) ? firstValue(option) : secondValue(option);
            applyOption(theme, option, value);
        });
        return new OptionBinding(checkbox, storedValue -> {
            checkbox.setValue(Objects.equals(storedValue, firstValue(option)));
            applyOption(theme, option, storedValue);
        });
    }

    private OptionBinding createColorBinding(ThemeDefinition theme, ThemeOption option) {
        Element input = new Element("input");
        input.setAttribute("type", "color");
        input.setAttribute("title", option.label());
        input.getClassList().add("theme-switcher-color-input");
        input.addEventListener("input", event -> {
            String value = event.getEventData().getString("event.target.value");
            applyOption(theme, option, value);
        }).addEventData("event.target.value");

        Span wrapper = new Span();
        wrapper.addClassName("theme-switcher-color-control");
        wrapper.getElement().appendChild(input);
        return new OptionBinding(wrapper, storedValue -> {
            input.setAttribute("value", storedValue);
            applyOption(theme, option, storedValue);
        });
    }

    private OptionBinding createActionBinding(ThemeDefinition theme, ThemeOption option) {
        Button button = new Button(option.label());
        button.addClassName("theme-switcher-option-control");
        button.addClickListener(event -> applyOption(theme, option, "action"));
        return new OptionBinding(button, storedValue -> applyOption(theme, option, storedValue));
    }

    private void applyOption(ThemeDefinition theme, ThemeOption option, String value) {
        applier.applyOption(option, value, appLayout);
        UI ui = UI.getCurrent();
        if (ui != null && persistenceEnabled && option.persistent() && !suppressOptionPersistence) {
            String storageKey = applier.storageKey(theme, option);
            if (value == null || value.isBlank()) {
                ui.getPage().executeJs("localStorage.removeItem($0)", storageKey);
            } else {
                ui.getPage().executeJs("localStorage.setItem($0, $1)", storageKey, value);
            }
        }
    }

    private void restoreSelectedThemeOrApplyCurrent(UI ui) {
        ui.getPage().executeJs("return localStorage.getItem($0)", selectedThemeStorageKey)
                .then(String.class, storedThemeId -> {
                    if (storedThemeId != null && !storedThemeId.isBlank()) {
                        setSelectedTheme(storedThemeId);
                    } else if (selectedTheme != null) {
                        setSelectedTheme(selectedTheme, false);
                    } else {
                        setSelectedTheme(fallbackThemeId);
                    }
                });
    }

    private void restoreOptionValue(ThemeDefinition theme, ThemeOption option, OptionBinding binding) {
        UI.getCurrent().getPage().executeJs("return localStorage.getItem($0)", applier.storageKey(theme, option))
                .then(String.class, value -> {
                    if (value != null && !value.isBlank()) {
                        binding.setStoredValue(value);
                    }
                });
    }

    private Optional<ThemeDefinition> findTheme(String themeId) {
        return themes.stream().filter(theme -> theme.id().equals(themeId)).findFirst();
    }

    private String firstValue(ThemeOption option) {
        return option.values().isEmpty() ? "true" : option.values().get(0).id();
    }

    private String secondValue(ThemeOption option) {
        return option.values().size() < 2 ? null : option.values().get(1).id();
    }

    private record OptionBinding(Component component, StoredValueSetter storedValueSetter) {

        void setStoredValue(String value) {
            storedValueSetter.set(value);
        }
    }

    @FunctionalInterface
    private interface StoredValueSetter {

        void set(String value);
    }
}
