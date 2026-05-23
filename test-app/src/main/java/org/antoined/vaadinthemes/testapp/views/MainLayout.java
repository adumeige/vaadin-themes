package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import org.antoined.vaadinthemes.fjord.FjordTheme;
import org.antoined.vaadinthemes.glass.GlassTheme;
import org.antoined.vaadinthemes.novelist.NovelistTheme;
import org.antoined.vaadinthemes.seagod.SeagodTheme;
import org.antoined.vaadinthemes.terminalsynth.TerminalSynthTheme;
import org.antoined.vaadinthemes.theme.BasicThemeDefinition;
import org.antoined.vaadinthemes.theme.BasicThemeOption;
import org.antoined.vaadinthemes.theme.ThemeDefinition;
import org.antoined.vaadinthemes.theme.ThemeOptionControl;
import org.antoined.vaadinthemes.theme.ThemeOptionTarget;
import org.antoined.vaadinthemes.theme.ThemeOptionValue;
import org.antoined.vaadinthemes.theme.ThemeSwitcher;

import java.util.List;

public class MainLayout extends AppLayout {

    public MainLayout() {
        var themeSwitcher = new ThemeSwitcher(themeDefinitions(), this);
        themeSwitcher.setSelectedThemeStorageKey("selected-theme");
        themeSwitcher.addThemeChangeListener(e -> applyPreviewDefaults(themeSwitcher, e.getTheme().id()));
        themeSwitcher.setSelectedTheme(TerminalSynthTheme.ID);
        themeSwitcher.setPersistenceEnabled(true);

        var toggle = new DrawerToggle();
        var title = new H1("Theme Test App");

        var navbar = new HorizontalLayout(toggle, title, themeSwitcher);
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

    private List<ThemeDefinition> themeDefinitions() {
        return List.of(
                lumoDefinition(),
                SeagodTheme.definition(),
                FjordTheme.definition(),
                TerminalSynthTheme.definition(),
                NovelistTheme.definition(),
                GlassTheme.definition());
    }

    private ThemeDefinition lumoDefinition() {
        return BasicThemeDefinition.of("lumo", "Lumo", null, List.of(
                BasicThemeOption.builder("mode", "Mode")
                        .control(ThemeOptionControl.SELECT)
                        .target(ThemeOptionTarget.ROOT_THEME_TOKEN)
                        .values(
                                ThemeOptionValue.of("light", "Light", ""),
                                ThemeOptionValue.of("dark", "Dark", "dark"))
                        .build()));
    }

    private void applyPreviewDefaults(ThemeSwitcher switcher, String themeId) {
        switch (themeId) {
            case "lumo" -> switcher.setOptionValue("mode", "light", false);
            case FjordTheme.ID -> {
                switcher.setOptionValue("mode", "dark", false);
                switcher.setOptionValue("density", "compact", false);
            }
            case TerminalSynthTheme.ID -> {
                switcher.setOptionValue("palette", "outrun", false);
                switcher.setOptionValue("density", "default", false);
                switcher.setOptionValue("scanlines", "off", false);
            }
            case NovelistTheme.ID -> {
                switcher.setOptionValue("palette", "codex", false);
                switcher.setOptionValue("mode", "light", false);
                switcher.setOptionValue("density", "regular", false);
            }
            case GlassTheme.ID -> {
                switcher.setOptionValue("mode", "light", false);
                switcher.setOptionValue("layout", "rounded", false);
                switcher.setOptionValue("glass", "standard", false);
                switcher.setOptionValue("backgroundMode", "aurora", false);
                switcher.setOptionValue("bg", "#e9eef5", false);
                switcher.setOptionValue("primary", "#4763e4", false);
                switcher.setOptionValue("secondary", "#64748b", false);
                switcher.setOptionValue("accent", "#f59e0b", false);
            }
            default -> {
            }
        }
    }
}
