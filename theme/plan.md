# Plan

This is a project for shared Java elements of the theme collection: tooling, metadata, and reusable Vaadin
components that make the CSS-only theme modules easier to preview and consume.

The theme-specific modules remain responsible for shipping CSS/assets under
`META-INF/resources/themes/<theme-name>/`. This module should not contain a visual theme of its own.

## Goals

- Centralize the theme switching and tweak wiring currently living in `test-app`'s `MainLayout`.
- Provide a small Java API that application code can use without knowing the exact HTML attributes, `data-*`
  attributes, CSS variable names, or stylesheet paths used by each theme.
- Keep the module Vaadin-only: no Spring dependency and no dependency on the preview app.
- Make each theme's options declarative enough that a generic switcher can render controls automatically.
- Let each theme module provide its own `ThemeDefinition` implementation or factory.
- Let each consuming app decide which theme definitions to include and in which order.
- Preserve app layout ownership: the helper can set theme tokens and HTML/AppLayout attributes, but it should not
  prescribe route structure, navigation, or page layout.

## Non-goals

- No CSS assets in this module.
- No route/view classes in this module.
- No theme compilation or bundling logic.
- No persistence beyond simple browser-side storage for selected theme and user tweaks.
- No attempt to replace Vaadin's `@Theme`; this module is for runtime preview/switching and option controls.
- No central built-in theme catalog in this module.
- No initial/default theme or option value selection in this module.

## Package

Use a package aligned with the existing project naming:

```text
org.antoined.vaadinthemes.theme
```

Potential subpackages:

- `org.antoined.vaadinthemes.theme.model`
- `org.antoined.vaadinthemes.theme.component`
- `org.antoined.vaadinthemes.theme.internal`

## Classes Of Note

### ThemeDefinition

Represents one available theme. Each theme module should provide its own implementation or factory for this interface.
Consuming apps build their own theme list by picking the definitions they want to expose.

Likely fields:

- `id`: stable machine name, such as `fjord`, `terminal-synth`, `novelist`, `glass`, or `lumo`.
- `label`: UI label, such as `Fjord` or `Terminal Synth`.
- `stylesheetPath`: optional runtime CSS path, such as `/themes/fjord/styles.css`; `Lumo` can use `null`.
- `options`: ordered list of `ThemeOption`.

Responsibilities:

- Describe what can be applied.
- Avoid directly touching `UI`, `Page`, or DOM state.
- Give the switcher enough metadata to render a useful UI.
- Avoid choosing app-level defaults. Theme and option selection belongs to the consuming app and user actions.

### ThemeOption

Interface that exposes a tweak made available by a specific theme. This allows the same tweak model to be rendered in
any UI.

Common option properties:

- `key`: stable persistence key, such as `mode`, `density`, `palette`, `glass`, or `vg-primary`.
- `label`: short UI label or tooltip.
- `storageKey`: optional override for browser persistence.
- `target`: where the value is applied.
- `values`: available values when the control is choice-based.

Useful target types:

- `ROOT_THEME_TOKEN`: add/remove a token in `document.documentElement.theme`.
- `ROOT_DATA_ATTRIBUTE`: set/remove `document.documentElement.dataset.<key>`.
- `ROOT_CSS_VARIABLE`: set/remove a CSS custom property on `document.documentElement.style`.
- `APP_LAYOUT_THEME_TOKEN`: add/remove a token on the provided `AppLayout`.
- `CUSTOM_ACTION`: callback for one-off actions such as toggling scanlines.

Useful option implementations:

- `ChoiceThemeOption`: renders as a `Select` or `ComboBox`.
- `ToggleThemeOption`: renders as a `Checkbox` or small toggle button.
- `ColorThemeOption`: renders as an `<input type="color">` backed by a CSS custom property.
- `ActionThemeOption`: renders as a `Button` for stateless or toggle-like actions.

### ThemeSwitcher

Reusable Vaadin component that lets users select a theme and optionally renders the active theme's options.

Responsibilities:

- Render theme selection from a list of `ThemeDefinition`.
- Apply the selected theme:
  - persist selected theme in `localStorage` only if persistence is enabled by the host app;
  - remove the previous runtime stylesheet link;
  - add the selected theme stylesheet link, when present;
  - reset known root attributes before applying the new theme;
  - apply option values selected by the user or supplied explicitly by the host app;
  - rebuild option controls for the selected theme.
- Restore the selected theme and saved option values only if the host app enables persistence.
- Expose change events so host apps can react to theme changes if needed.
- Do not select a theme automatically when no persisted or explicit selection exists.

Constructor/factory ideas:

```java
ThemeSwitcher switcher = new ThemeSwitcher(List.of(...), appLayout);
ThemeSwitcher switcher = new ThemeSwitcher(List.of(FjordTheme.definition(), GlassTheme.definition()), appLayout);
switcher.setSelectedTheme("glass");
```

The `AppLayout` reference should be optional. Without it, `APP_LAYOUT_THEME_TOKEN` options are ignored or disabled.

### ThemeApplier

Small internal service that owns the JavaScript bridge.

Responsibilities:

- Keep the script fragments isolated from component rendering code.
- Apply a `ThemeDefinition`.
- Apply a `ThemeOption` value.
- Clear root state between themes.
- Read/write browser storage.

This should be package-private or placed under `internal` unless there is a strong reason to expose it.

## Public API Sketch

Minimum useful surface:

```java
public final class ThemeSwitcher extends Composite<HorizontalLayout> {
    public ThemeSwitcher(List<ThemeDefinition> themes);
    public ThemeSwitcher(List<ThemeDefinition> themes, AppLayout appLayout);
    public Registration addThemeChangeListener(ComponentEventListener<ThemeChangeEvent> listener);
    public void setSelectedTheme(String themeId);
}

public interface ThemeOption {
    String key();
    String label();
    ThemeOptionTarget target();
}
```

Keep builders optional until the metadata starts getting noisy. Plain records may be enough for the first pass.

## Integration Plan

1. Add Vaadin component dependencies to `theme/pom.xml`.
2. Implement the shared model classes.
3. Implement `ThemeApplier` with the same behavior currently in `MainLayout.applyTheme`, but generalized.
4. Implement `ThemeSwitcher` and option rendering.
5. Add one `ThemeDefinition` implementation or factory in each theme module that wants to expose runtime options.
6. Add `test-app` dependencies on `theme` and the desired theme modules.
7. Build the test app's theme list explicitly in `MainLayout`.
8. Replace the hand-built switcher and tweak methods in `test-app/MainLayout` with the shared component.
9. Update README usage examples once the API settles.

## Testing Plan

- Unit-test metadata construction where practical.
- Add focused tests for generated JavaScript inputs/escaping if `ThemeApplier` builds script strings.
- Run `mvn validate -pl theme` after skeleton changes.
- Run `mvn test -pl theme,test-app` once tests exist.
- Smoke-test the preview app manually:
  - theme restore from `localStorage` when persistence is enabled;
  - switching from each custom theme back to Lumo;
  - options reset correctly when changing themes;
  - Glass layout toggle only affects the host `AppLayout`;
  - color CSS variables persist and reapply when persistence is enabled.

## Open Questions

- Should `ThemeSwitcher` render as a compact horizontal toolbar, or expose separate `themeSelector()` and
  `optionsBar()` components for host layout control?
- Should persistence be opt-in at the switcher level, per option, or both?
- Should `ThemeDefinition` include Maven/artifact metadata for documentation and dependency snippets?
- Should stylesheet injection support multiple CSS files per theme, or is one `styles.css` enough for this collection?
- Should option controls use `Select` for compact app chrome, or allow a renderer strategy for richer UIs?
- Should theme modules expose definitions as `public static ThemeDefinition definition()` factories, singleton constants,
  or service-loader providers?
