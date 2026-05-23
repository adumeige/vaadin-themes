package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MainLayout extends AppLayout {

    private final HorizontalLayout tweaksArea = new HorizontalLayout();

    public MainLayout() {
        tweaksArea.setAlignItems(FlexComponent.Alignment.CENTER);
        tweaksArea.setSpacing(true);

        // Theme switcher
        var themeCombo = new ComboBox<String>();
        themeCombo.setItems("Lumo", "Seagod", "Fjord", "Terminal Synth", "Novelist", "Glass");
        themeCombo.setWidth("160px");
        themeCombo.setPlaceholder("Theme...");
        themeCombo.setClearButtonVisible(false);

        themeCombo.addValueChangeListener(e -> {
            if (e.getValue() != null && e.isFromClient()) {
                applyTheme(e.getValue());
                buildTweaks(e.getValue());
            }
        });

        // Restore saved theme on load
        UI.getCurrent().getPage().executeJs(
                  "return localStorage.getItem('selected-theme') || 'Terminal Synth'")
          .then(String.class, savedTheme -> {
              themeCombo.setValue(savedTheme);
              applyTheme(savedTheme);
              buildTweaks(savedTheme);
          });

        var toggle = new DrawerToggle();
        var title = new H1("Theme Test App");

        var navbar = new HorizontalLayout(toggle, title, tweaksArea, themeCombo);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.expand(title);
        navbar.getStyle().set("padding-right", "16px");

        addToNavbar(navbar);

        var workspace = new SideNav();
        workspace.setLabel("Workspace");
        workspace.addItem(new SideNavItem("Customers", org.antoined.vaadinthemes.testapp.views.glass.CustomersView.class, VaadinIcon.USERS.create()));
        workspace.addItem(new SideNavItem("Insights",  org.antoined.vaadinthemes.testapp.views.glass.InsightsView.class,  VaadinIcon.BAR_CHART_V.create()));
        workspace.addItem(new SideNavItem("Settings",  org.antoined.vaadinthemes.testapp.views.glass.SettingsView.class,  VaadinIcon.COG.create()));

        var demos = new SideNav();
        demos.setLabel("Demos");
        demos.addItem(new SideNavItem("Signal Dashboard", SignalDashboardView.class, VaadinIcon.SIGNAL.create()));
        demos.addItem(new SideNavItem("Terminal", TerminalView.class, VaadinIcon.TERMINAL.create()));
        demos.addItem(new SideNavItem("AI Assistant", ChatbotView.class, VaadinIcon.CHAT.create()));

        var components = new SideNav();
        components.setLabel("Components");
        components.addItem(new SideNavItem("Buttons & Inputs", ButtonsAndInputsView.class, VaadinIcon.EDIT.create()));
        components.addItem(new SideNavItem("Data Display", DataDisplayView.class, VaadinIcon.TABLE.create()));
        components.addItem(new SideNavItem("Feedback & Overlay", FeedbackAndOverlayView.class, VaadinIcon.BELL.create()));
        components.addItem(new SideNavItem("Layout & Navigation", LayoutAndNavigationView.class, VaadinIcon.LAYOUT.create()));

        addToDrawer(workspace, demos, components);


    }

    private void applyTheme(String themeName) {
        // Clear any AppLayout-level variant from a previous theme.
        getElement().removeAttribute("theme");

        String cssPath = switch (themeName) {
            case "Seagod" -> "/themes/seagod/styles.css";
            case "Fjord" -> "/themes/fjord/styles.css";
            case "Terminal Synth" -> "/themes/terminal-synth/styles.css";
            case "Novelist" -> "/themes/novelist/styles.css";
            case "Glass" -> "/themes/glass/styles.css";
            default -> null;
        };

        String themeAttr = switch (themeName) {
            case "Fjord" -> "dark compact";
            case "Terminal Synth" -> "dark";
            case "Glass" -> "light";
            default -> "";
        };

        UI.getCurrent().getPage().executeJs(
                "localStorage.setItem('selected-theme', $0);" +

                "const existing = document.getElementById('custom-theme-css');" +
                "if (existing) existing.remove();" +

                "const h = document.documentElement;" +
                "h.removeAttribute('data-palette');" +
                "h.removeAttribute('data-density');" +
                "h.removeAttribute('data-mode');" +
                "h.removeAttribute('data-scanlines');" +
                "h.removeAttribute('data-radius');" +
                "h.removeAttribute('data-glass');" +

                "if ($2) { h.setAttribute('theme', $2); } else { h.removeAttribute('theme'); }" +

                "if ($1) {" +
                "  const link = document.createElement('link');" +
                "  link.id = 'custom-theme-css';" +
                "  link.rel = 'stylesheet';" +
                "  link.href = $1;" +
                "  document.head.appendChild(link);" +
                "}",
                themeName, cssPath, themeAttr);
    }

    private void buildFjordTweaks() {
        var mode = new Select<String>();
        mode.setItems("Light", "Dark");
        mode.setValue("Dark");
        mode.setWidth("90px");
        mode.addValueChangeListener(e -> {
            String density = "compact"; // keep current density
            String theme = ("Dark".equals(e.getValue()) ? "dark" : "light") + " " + density;
            setAttr("theme", theme);
        });

        var density = new Select<String>();
        density.setItems("Comfy", "Standard", "Compact");
        density.setValue("Compact");
        density.setWidth("110px");
        density.addValueChangeListener(e -> {
            String d = e.getValue().toLowerCase();
            // Read current mode from the mode select
            UI.getCurrent().getPage().executeJs(
                    "const cur = document.documentElement.getAttribute('theme') || '';" +
                    "const isDark = cur.includes('dark');" +
                    "document.documentElement.setAttribute('theme', (isDark ? 'dark' : 'light') + ' ' + $0);",
                    d);
        });

        tweaksArea.add(mode, density);
    }

    private void buildLumoTweaks() {
        var mode = new Select<String>();
        mode.setItems("Light", "Dark");
        mode.setValue("Light");
        mode.setWidth("90px");
        mode.addValueChangeListener(e -> setAttr("theme", "Dark".equals(e.getValue()) ? "dark" : ""));
        tweaksArea.add(mode);
    }

    private void buildNovelistTweaks() {
        var palette = new Select<String>();
        palette.setItems("Codex", "Scriptorium", "Saga");
        palette.setValue("Codex");
        palette.setWidth("130px");
        palette.addValueChangeListener(e -> setData("palette", e.getValue().toLowerCase()));

        var mode = new Select<String>();
        mode.setItems("Light", "Dark");
        mode.setValue("Light");
        mode.setWidth("90px");
        mode.addValueChangeListener(e -> setData("mode", e.getValue().toLowerCase()));

        var density = new Select<String>();
        density.setItems("Compact", "Regular", "Comfortable");
        density.setValue("Regular");
        density.setWidth("120px");
        density.addValueChangeListener(e -> {
            String val = "Regular".equals(e.getValue()) ? "" : e.getValue().toLowerCase();
            if (val.isEmpty()) {
                UI.getCurrent().getPage().executeJs("document.documentElement.removeAttribute('data-density')");
            } else {
                setData("density", val);
            }
        });

        tweaksArea.add(palette, mode, density);
    }

    private void buildSeagodTweaks() {
        // Seagod is light-only — no tweaks
    }

    private void buildGlassTweaks() {
        var mode = new Select<String>();
        mode.setItems("Light", "Dark");
        mode.setValue("Light");
        mode.setWidth("90px");
        mode.addValueChangeListener(e -> setAttr("theme", "Dark".equals(e.getValue()) ? "dark" : "light"));

        var bgVariant = new Select<String>();
        bgVariant.setItems("Flat", "Glass");
        bgVariant.setValue("Flat");
        bgVariant.setWidth("90px");
        bgVariant.addValueChangeListener(e -> {
            if ("Glass".equals(e.getValue())) {
                getElement().setAttribute("theme", "glass-bg");
            } else {
                getElement().removeAttribute("theme");
            }
        });

        var glass = new Select<String>();
        glass.setItems("Subtle", "Standard", "Heavy");
        glass.setValue("Standard");
        glass.setWidth("110px");
        glass.addValueChangeListener(e -> {
            String v = e.getValue();
            if ("Standard".equals(v)) {
                UI.getCurrent().getPage().executeJs("document.documentElement.removeAttribute('data-glass')");
            } else {
                setData("glass", v.toLowerCase());
            }
        });

        var bg        = colorSwatch("--vg-bg",        "#e9eef5", "Background");
        var primary   = colorSwatch("--vg-primary",   "#4763e4", "Primary");
        var secondary = colorSwatch("--vg-secondary", "#64748b", "Secondary");
        var accent    = colorSwatch("--vg-accent",    "#f59e0b", "Accent");

        // Restore any previously saved color tweaks so reloading the page
        // (or switching back to Glass) keeps the user's palette.
        UI.getCurrent().getPage().executeJs(
                "const tokens = ['--vg-bg','--vg-primary','--vg-secondary','--vg-accent'];" +
                "tokens.forEach(t => {" +
                "  const v = localStorage.getItem('glass-' + t);" +
                "  if (v) document.documentElement.style.setProperty(t, v);" +
                "});");

        tweaksArea.add(mode, bgVariant, glass, bg, primary, secondary, accent);
    }

    /** Small <input type="color"> swatch that writes the chosen value
     *  straight to the matching --vg-* token and persists it. */
    private com.vaadin.flow.component.html.Span colorSwatch(String token, String defaultHex, String tooltip) {
        com.vaadin.flow.dom.Element input = new com.vaadin.flow.dom.Element("input");
        input.setAttribute("type", "color");
        input.setAttribute("value", defaultHex);
        input.setAttribute("title", tooltip);
        input.getStyle().set("width", "28px").set("height", "28px").set("padding", "0")
                .set("border", "1px solid var(--lumo-contrast-30pct)")
                .set("border-radius", "6px").set("cursor", "pointer")
                .set("background", "transparent");
        input.addEventListener("input", e -> {
            String v = e.getEventData().getString("event.target.value");
            UI.getCurrent().getPage().executeJs(
                    "document.documentElement.style.setProperty($0, $1);" +
                    "localStorage.setItem('glass-' + $0, $1);",
                    token, v);
        }).addEventData("event.target.value");
        com.vaadin.flow.component.html.Span wrap = new com.vaadin.flow.component.html.Span();
        wrap.getElement().appendChild(input);
        return wrap;
    }

    private void buildTerminalSynthTweaks() {
        var palette = new Select<String>();
        palette.setItems("Outrun", "Miami", "Tokyo", "Dusk");
        palette.setValue("Outrun");
        palette.setWidth("105px");
        palette.addValueChangeListener(e -> setData("palette", e.getValue().toLowerCase()));

        var density = new Select<String>();
        density.setItems("Compact", "Default", "Comfortable");
        density.setValue("Default");
        density.setWidth("120px");
        density.addValueChangeListener(e -> setData("density", e.getValue().toLowerCase()));

        var scanlines = new Button("CRT");
        scanlines.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        scanlines.setTooltipText("Toggle scanlines");
        scanlines.addClickListener(e -> {
            UI.getCurrent().getPage().executeJs(
                    "const h = document.documentElement;" +
                    "const on = h.getAttribute('data-scanlines') === 'on';" +
                    "h.setAttribute('data-scanlines', on ? 'off' : 'on');");
        });

        tweaksArea.add(palette, density, scanlines);
    }

    private void buildTweaks(String themeName) {
        tweaksArea.removeAll();

        switch (themeName) {
            case "Lumo" -> buildLumoTweaks();
            case "Seagod" -> buildSeagodTweaks();
            case "Fjord" -> buildFjordTweaks();
            case "Terminal Synth" -> buildTerminalSynthTweaks();
            case "Novelist" -> buildNovelistTweaks();
            case "Glass" -> buildGlassTweaks();
        }
    }

    private void setAttr(String attr, String value) {
        if (value == null || value.isEmpty()) {
            UI.getCurrent().getPage().executeJs(
                    "document.documentElement.removeAttribute($0)", attr);
        } else {
            UI.getCurrent().getPage().executeJs(
                    "document.documentElement.setAttribute($0, $1)", attr, value);
        }
    }

    private void setData(String key, String value) {
        UI.getCurrent().getPage().executeJs(
                "document.documentElement.setAttribute('data-' + $0, $1)", key, value);
    }
}
