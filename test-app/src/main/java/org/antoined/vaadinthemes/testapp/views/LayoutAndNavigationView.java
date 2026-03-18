package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "layout-navigation", layout = MainLayout.class)
@PageTitle("Layout & Navigation")
public class LayoutAndNavigationView extends VerticalLayout {

    public LayoutAndNavigationView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Layout & Navigation Components"));

        // --- Tabs ---
        add(new H3("Tabs"));
        var tabs = new Tabs();
        tabs.add(new Tab(VaadinIcon.HOME.create(), new Span("Home")));
        tabs.add(new Tab(VaadinIcon.USER.create(), new Span("Profile")));
        tabs.add(new Tab(VaadinIcon.COG.create(), new Span("Settings")));
        tabs.add(new Tab(VaadinIcon.BELL.create(), new Span("Notifications")));
        var disabledTab = new Tab("Disabled");
        disabledTab.setEnabled(false);
        tabs.add(disabledTab);
        add(tabs);

        // Small variant
        var smallTabs = new Tabs();
        smallTabs.addThemeVariants(TabsVariant.LUMO_SMALL);
        smallTabs.add(new Tab("Small 1"), new Tab("Small 2"), new Tab("Small 3"));
        add(smallTabs);

        add(new Hr());

        // --- TabSheet ---
        add(new H3("TabSheet"));
        var tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.add("Dashboard", new VerticalLayout(
                new Paragraph("Dashboard content goes here. This shows an overview of your data."),
                createCard("Revenue", "$12,345", "success"),
                createCard("Users", "1,234", "primary"),
                createCard("Orders", "567", "contrast")
        ));
        tabSheet.add("Analytics", new VerticalLayout(
                new Paragraph("Analytics content with charts and graphs would appear here.")
        ));
        tabSheet.add("Reports", new VerticalLayout(
                new Paragraph("Report generation and download options.")
        ));
        add(tabSheet);

        add(new Hr());

        // --- Split Layout ---
        add(new H3("Split Layout"));
        var splitLayout = new SplitLayout();
        splitLayout.setWidthFull();
        splitLayout.setHeight("200px");

        var leftPanel = new VerticalLayout();
        leftPanel.add(new H3("Left Panel"));
        leftPanel.add(new Paragraph("Resize the splitter to see the split layout in action."));

        var rightPanel = new VerticalLayout();
        rightPanel.add(new H3("Right Panel"));
        rightPanel.add(new Paragraph("Content on the right side of the split."));

        splitLayout.addToPrimary(leftPanel);
        splitLayout.addToSecondary(rightPanel);
        splitLayout.setSplitterPosition(40);
        add(splitLayout);

        // Vertical split
        var verticalSplit = new SplitLayout(SplitLayout.Orientation.VERTICAL);
        verticalSplit.setWidthFull();
        verticalSplit.setHeight("200px");

        var topPanel = new VerticalLayout(new Span("Top panel"));
        var bottomPanel = new VerticalLayout(new Span("Bottom panel"));
        verticalSplit.addToPrimary(topPanel);
        verticalSplit.addToSecondary(bottomPanel);
        add(verticalSplit);

        add(new Hr());

        // --- Scroller ---
        add(new H3("Scroller"));
        var scroller = new Scroller();
        var scrollContent = new VerticalLayout();
        for (int i = 1; i <= 20; i++) {
            scrollContent.add(new Paragraph("Scrollable item #" + i));
        }
        scroller.setContent(scrollContent);
        scroller.setHeight("200px");
        scroller.setWidthFull();
        scroller.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        add(scroller);

        add(new Hr());

        // --- Cards (using Div with Lumo styling) ---
        add(new H3("Card-like Layouts"));
        var cardRow = new FlexLayout();
        cardRow.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cardRow.getStyle().set("gap", "var(--lumo-space-m)");

        cardRow.add(createStyledCard("Users", "Total active users in the system", "1,247", VaadinIcon.USERS));
        cardRow.add(createStyledCard("Revenue", "Monthly recurring revenue", "$45,231", VaadinIcon.DOLLAR));
        cardRow.add(createStyledCard("Tasks", "Open tasks this sprint", "23", VaadinIcon.TASKS));
        cardRow.add(createStyledCard("Uptime", "System availability", "99.9%", VaadinIcon.SERVER));

        add(cardRow);
    }

    private Div createCard(String title, String value, String theme) {
        var card = new Div();
        card.getStyle()
                .set("display", "inline-flex")
                .set("gap", "var(--lumo-space-s)")
                .set("align-items", "center");

        var badge = new Span(value);
        badge.getElement().getThemeList().add("badge " + theme);

        card.add(new Span(title + ": "), badge);
        return card;
    }

    private Div createStyledCard(String title, String description, String value, VaadinIcon icon) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("padding", "var(--lumo-space-l)")
                .set("min-width", "200px")
                .set("flex", "1");

        var headerRow = new HorizontalLayout();
        headerRow.setAlignItems(Alignment.CENTER);
        headerRow.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerRow.setWidthFull();

        var titleSpan = new Span(title);
        titleSpan.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size", "var(--lumo-font-size-s)");

        var iconEl = icon.create();
        iconEl.setSize("var(--lumo-font-size-l)");
        iconEl.getStyle().set("color", "var(--lumo-primary-color)");

        headerRow.add(titleSpan, iconEl);

        var valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "var(--lumo-font-size-xxl)").set("font-weight", "bold");

        var descSpan = new Span(description);
        descSpan.getStyle().set("color", "var(--lumo-tertiary-text-color)").set("font-size", "var(--lumo-font-size-xs)");

        var layout = new VerticalLayout(headerRow, valueSpan, descSpan);
        layout.setPadding(false);
        layout.setSpacing(false);
        card.add(layout);

        return card;
    }
}
