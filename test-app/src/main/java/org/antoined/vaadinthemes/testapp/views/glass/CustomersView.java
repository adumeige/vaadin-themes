package org.antoined.vaadinthemes.testapp.views.glass;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.antoined.vaadinthemes.testapp.views.MainLayout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Route(value = "glass/customers", layout = MainLayout.class)
@PageTitle("Customers — Glass")
public class CustomersView extends VerticalLayout {

    private record Customer(String id, String name, String contact, int revenue,
                            String status, String initials, int hue) {}

    private static final List<Customer> CUSTOMERS = List.of(
            new Customer("acme",    "Acme Industrial",    "ops@acme.co",       184500, "Premium",  "AI", 215),
            new Customer("northw",  "Northwind Traders",  "logistics@nw.io",   142800, "Premium",  "NT", 145),
            new Customer("vertex",  "Vertex Logistics",   "billing@vertex.eu",  98200, "Standard", "VL", 280),
            new Customer("globex",  "Globex Corporation", "ops@globex.com",     67400, "Standard", "GC",  25),
            new Customer("initech", "Initech Systems",    "hello@initech.io",   58900, "Standard", "IS", 195),
            new Customer("hooli",   "Hooli Inc.",         "ops@hooli.xyz",      44100, "Trial",    "HI", 330),
            new Customer("piper",   "Pied Piper",         "logistics@pp.com",   41200, "Premium",  "PP",  95),
            new Customer("massive", "Massive Dynamic",    "billing@md.com",     32400, "Standard", "MD", 240),
            new Customer("wonka",   "Wonka Industries",   "ops@wonka.co",       28100, "Standard", "WI",  50),
            new Customer("stark",   "Stark Industries",   "hello@stark.com",    24700, "Trial",    "SI",   5)
    );

    private final VerticalLayout listColumn = new VerticalLayout();
    private final VerticalLayout detailHeaderSlot = new VerticalLayout();
    private final VerticalLayout detailTabsSlot = new VerticalLayout();
    private final VerticalLayout tabContent = new VerticalLayout();
    private final VerticalLayout actionBarSlot = new VerticalLayout();
    private Customer current = CUSTOMERS.get(0);
    private String activeTab = "profile";
    private String searchTerm = "";

    public CustomersView() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        add(buildPageHead());
        add(buildSplit());

        rebuildList();
        rebuildDetailHeader();
        rebuildTabs();
        rebuildTabContent();
        rebuildActionBar();
    }

    private Component buildPageHead() {
        H1 title = new H1("Customers");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.SEMIBOLD);
        title.getStyle().set("letter-spacing", "-0.02em");
        int totalK = CUSTOMERS.stream().mapToInt(Customer::revenue).sum() / 1000;
        Span sub = new Span(CUSTOMERS.size() + " accounts · €" + totalK + "k MTD revenue");
        sub.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.SECONDARY);
        VerticalLayout meta = new VerticalLayout(title, sub);
        meta.setPadding(false);
        meta.setSpacing(false);
        meta.getStyle().set("gap", "6px");

        Button exportBtn = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        Button newBtn = new Button("New customer", new Icon(VaadinIcon.PLUS));
        newBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        HorizontalLayout actions = new HorizontalLayout(exportBtn, newBtn);
        actions.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout head = new HorizontalLayout(meta, actions);
        head.setWidthFull();
        head.setAlignItems(FlexComponent.Alignment.END);
        head.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        head.getStyle()
                .set("padding", "24px 28px 18px")
                .set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)")
                .set("flex", "0 0 auto");
        return head;
    }

    // ── Master-detail split ────────────────────────────────────────────

    private Component buildSplit() {
        SplitLayout split = new SplitLayout();
        split.setSizeFull();
        split.setSplitterPosition(25);
        split.getStyle().set("flex", "1").set("min-height", "0");

        // Left: search header + scrollable list
        TextField search = new TextField();
        search.setPlaceholder("Search customers…");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setWidthFull();
        search.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.LAZY);
        search.addValueChangeListener(e -> {
            searchTerm = e.getValue() == null ? "" : e.getValue().toLowerCase();
            rebuildList();
        });
        Button filter = new Button(new Icon(VaadinIcon.FILTER));
        filter.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        HorizontalLayout searchRow = new HorizontalLayout(search, filter);
        searchRow.setWidthFull();
        searchRow.setAlignItems(FlexComponent.Alignment.CENTER);
        searchRow.expand(search);
        searchRow.getStyle()
                .set("padding", "14px 16px")
                .set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");

        listColumn.setPadding(false);
        listColumn.setSpacing(false);
        listColumn.getStyle().set("padding", "6px 8px");
        Scroller listScroller = new Scroller(listColumn);
        listScroller.setSizeFull();
        listScroller.getStyle().set("flex", "1").set("min-height", "0");

        VerticalLayout left = new VerticalLayout(searchRow, listScroller);
        left.setPadding(false);
        left.setSpacing(false);
        left.setSizeFull();

        // Right: detail header, tabs, tab content (scrolling), action bar
        detailHeaderSlot.setPadding(false);
        detailHeaderSlot.setSpacing(false);

        detailTabsSlot.setPadding(false);
        detailTabsSlot.setSpacing(false);

        tabContent.setPadding(false);
        tabContent.setSpacing(false);
        Scroller tabScroller = new Scroller(tabContent);
        tabScroller.setSizeFull();
        tabScroller.getStyle().set("flex", "1").set("min-height", "0");

        actionBarSlot.setPadding(false);
        actionBarSlot.setSpacing(false);

        VerticalLayout right = new VerticalLayout(detailHeaderSlot, detailTabsSlot, tabScroller, actionBarSlot);
        right.setPadding(false);
        right.setSpacing(false);
        right.setSizeFull();

        split.addToPrimary(left);
        split.addToSecondary(right);
        return split;
    }

    private void rebuildList() {
        listColumn.removeAll();
        for (Customer c : CUSTOMERS) {
            if (!searchTerm.isEmpty() && !c.name.toLowerCase().contains(searchTerm)) continue;

            Avatar av = new Avatar(c.name, c.initials);
            av.getStyle()
                    .set("--vaadin-avatar-size", "30px")
                    .set("background",
                            "linear-gradient(135deg, hsl(" + c.hue + " 65% 55%), hsl(" + ((c.hue + 30) % 360) + " 65% 45%))")
                    .set("color", "white");

            Span name = new Span(c.name);
            name.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.MEDIUM);
            name.getStyle().set("white-space", "nowrap").set("text-overflow", "ellipsis").set("overflow", "hidden");
            Span contact = new Span(c.contact);
            contact.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);

            VerticalLayout meta = new VerticalLayout(name, contact);
            meta.setPadding(false);
            meta.setSpacing(false);

            Span trail = new Span("€" + (c.revenue / 1000) + "k");
            trail.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);
            trail.getStyle().set("font-variant-numeric", "tabular-nums");

            HorizontalLayout row = new HorizontalLayout(av, meta, trail);
            row.setWidthFull();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.expand(meta);
            row.setClassName("vg-list-item" + (current.id.equals(c.id) ? " is-active" : ""));
            row.getStyle().set("padding", "8px 10px").set("cursor", "pointer");
            row.addClickListener(e -> {
                current = c;
                rebuildList();
                rebuildDetailHeader();
                rebuildTabContent();
                rebuildActionBar();
            });
            listColumn.add(row);
        }
    }

    private void rebuildDetailHeader() {
        detailHeaderSlot.removeAll();

        Avatar av = new Avatar(current.name, current.initials);
        av.getStyle()
                .set("--vaadin-avatar-size", "44px")
                .set("background",
                        "linear-gradient(135deg, hsl(" + current.hue + " 65% 55%), hsl(" + ((current.hue + 30) % 360) + " 65% 45%))")
                .set("color", "white");

        H2 h = new H2(current.name);
        h.getStyle().set("margin", "0").set("font-size", "20px").set("font-weight", "600").set("letter-spacing", "-0.015em");
        Span statusPill = new Span(current.status);
        statusPill.setClassName("vg-status vg-status-" + statusKind(current.status));
        HorizontalLayout titleRow = new HorizontalLayout(h, statusPill);
        titleRow.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout meta = new HorizontalLayout(
                new Span("ID · " + current.id.toUpperCase()),
                dot(),
                new Span("Customer since 2019"),
                dot(),
                new Span("€" + String.format("%,d", current.revenue) + " MTD"));
        meta.setAlignItems(FlexComponent.Alignment.CENTER);
        meta.setSpacing(true);
        meta.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        VerticalLayout nameBlock = new VerticalLayout(titleRow, meta);
        nameBlock.setPadding(false);
        nameBlock.setSpacing(false);
        nameBlock.getStyle().set("gap", "4px");

        HorizontalLayout left = new HorizontalLayout(av, nameBlock);
        left.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout iconActions = new HorizontalLayout();
        iconActions.setSpacing(false);
        for (VaadinIcon ic : List.of(VaadinIcon.COPY, VaadinIcon.INBOX, VaadinIcon.ELLIPSIS_DOTS_H)) {
            Button b = new Button(new Icon(ic));
            b.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
            iconActions.add(b);
        }

        HorizontalLayout row = new HorizontalLayout(left, iconActions);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.START);
        row.getStyle().set("padding", "20px 24px 0");
        detailHeaderSlot.add(row);
    }

    private void rebuildTabs() {
        detailTabsSlot.removeAll();
        Tabs tabs = new Tabs(
                tab("profile",   "Profile",   null),
                tab("contracts", "Contracts", 3),
                tab("activity",  "Activity",  28),
                tab("billing",   "Billing",   null));
        tabs.setSelectedIndex(indexOf(activeTab, tabs));
        tabs.addSelectedChangeListener(e -> {
            activeTab = e.getSelectedTab().getId().orElse("profile");
            rebuildTabContent();
            rebuildActionBar();
        });
        detailTabsSlot.getStyle().set("padding", "12px 24px 0");
        detailTabsSlot.add(tabs);
    }

    private int indexOf(String id, Tabs tabs) {
        for (int i = 0; i < tabs.getComponentCount(); i++) {
            if (tabs.getComponentAt(i).getId().orElse("").equals(id)) return i;
        }
        return 0;
    }

    private Tab tab(String id, String label, Integer count) {
        Tab t = new Tab(label);
        t.setId(id);
        if (count != null) {
            Span c = new Span(String.valueOf(count));
            c.getStyle()
                    .set("margin-left", "6px").set("font-size", "10px").set("padding", "0 5px")
                    .set("border-radius", "999px")
                    .set("background", "var(--lumo-contrast-10pct)")
                    .set("color", "var(--lumo-secondary-text-color)");
            t.add(c);
        }
        return t;
    }

    private void rebuildTabContent() {
        tabContent.removeAll();
        switch (activeTab) {
            case "profile":   buildProfile();   break;
            case "contracts": buildContracts(); break;
            case "activity":  buildActivity();  break;
            case "billing":   buildBilling();   break;
        }
    }

    private void rebuildActionBar() {
        actionBarSlot.removeAll();
        if (!"profile".equals(activeTab)) return;

        Icon info = new Icon(VaadinIcon.INFO_CIRCLE_O);
        info.setSize("14px");
        Span left = new Span("Last saved 2 min ago by Jana Müller");
        left.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        HorizontalLayout leftRow = new HorizontalLayout(info, left);
        leftRow.setAlignItems(FlexComponent.Alignment.CENTER);

        Button discard = new Button("Discard changes");
        discard.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        discard.addClickListener(e -> {
            ConfirmDialog cd = new ConfirmDialog();
            cd.setHeader("Discard unsaved changes?");
            cd.setText("Your edits to this customer will be lost. This cannot be undone.");
            cd.setCancelable(true);
            cd.setConfirmText("Discard");
            cd.setConfirmButtonTheme("primary error");
            cd.addConfirmListener(ev -> toast("Changes discarded", "", NotificationVariant.LUMO_CONTRAST));
            cd.open();
        });
        Button save = new Button("Save changes", new Icon(VaadinIcon.CHECK));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> toast("Customer saved", "Updated " + current.name + " profile.", NotificationVariant.LUMO_SUCCESS));

        HorizontalLayout bar = new HorizontalLayout(leftRow, new HorizontalLayout(discard, save));
        bar.setWidthFull();
        bar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        bar.setClassName("vg-action-bar");
        bar.getStyle().set("padding", "12px 24px");
        actionBarSlot.add(bar);
    }

    // ── Tabs ───────────────────────────────────────────────────────────

    private void buildProfile() {
        // Info banner
        Icon ic = new Icon(VaadinIcon.INFO_CIRCLE_O);
        ic.setSize("14px");
        ic.getStyle().set("color", "var(--lumo-primary-color)").set("flex-shrink", "0");
        Span msg = new Span();
        msg.getElement().setProperty("innerHTML",
                "<b>Profile auto-sync enabled.</b> Contact details are kept in sync with the CRM record.");
        HorizontalLayout banner = new HorizontalLayout(ic, msg);
        banner.setClassName("vg-banner");
        banner.setAlignItems(FlexComponent.Alignment.START);
        banner.getStyle().set("padding", "10px 14px").set("margin", "16px 24px 0");
        tabContent.add(banner);

        tabContent.add(section("Company", "Legal entity and account ownership.", "Auto-saved", form(
                labeled("Company name", textField(current.name), null),
                labeled("Tax / VAT ID", textField("DE 814 583 901"), "Used on invoices and shipping documents."),
                labeled("Industry", industrySelect(), null),
                labeled("Founded", textFieldWithPrefix("Mar 14, 2008", new Icon(VaadinIcon.CALENDAR)), null),
                labeled("Employees", employeesField(), null),
                labeled("Account manager", managerSelect(), null)
        )));

        tabContent.add(section("Primary contact", "Who we reach for dispatch confirmations.", null, form(
                labeled("Full name", textFieldWithPrefix("Marco Brenner", new Icon(VaadinIcon.USER)), null),
                labeled("Email",     emailField(current.contact), null),
                labeled("Phone",     textField("+49 30 5588 4421"), "Include country code"),
                labeled("Preferred channel", channelRadio(), null)
        )));

        FormLayout services = form(
                labeledSpan("Delivery window", windowRadio(), null),
                labeledSpan("Notification channels", channelsCheck(), null),
                labeledSpan("Special handling", handlingMulti(), "Tags applied to every shipment for this customer."),
                labeledSpan("Priority", priorityRadio(), null)
        );
        tabContent.add(section("Service preferences", "How shipments are scheduled and handled.", null, services));

        TextArea notes = new TextArea("Notes");
        notes.setHelperText("Visible to dispatch team only.");
        notes.setWidthFull();
        notes.setMinHeight("84px");
        notes.setValue("Customer prefers consolidated weekly invoices. Carrier preference DHL. " +
                "Receiving hours Mon–Fri 7:00–15:00.");
        VerticalLayout notesSection = new VerticalLayout(notes);
        notesSection.setPadding(false);
        notesSection.setSpacing(false);
        notesSection.getStyle().set("padding", "22px 24px").set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");
        tabContent.add(notesSection);
    }

    private void buildContracts() {
        VerticalLayout root = new VerticalLayout();
        root.setPadding(true);
        root.setSpacing(true);
        root.getStyle().set("padding", "20px 24px");
        record C(String title, String status, String end, String value) {}
        for (C c : List.of(
                new C("Master Services Agreement", "Active", "Dec 2026", "€12,500/mo"),
                new C("Cold Chain Addendum",       "Active", "Mar 2027", "€2,800/mo"),
                new C("Express Lane Pilot",        "Draft",  "—",        "TBD"))) {
            Card card = new Card();
        card.setWidthFull();
            card.setTitle(c.title);
            Span sub = new Span("Renews " + c.end + " · " + c.value);
            sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
            card.setSubtitle(sub);
            Span st = new Span(c.status);
            st.setClassName("vg-status vg-status-" + ("Active".equals(c.status) ? "delivered" : "pending"));
            card.setHeaderSuffix(st);
            Span body = new Span("Standard SLA · 24h dispatch · Net 30 payment terms. View the full document " +
                    "in the documents section or download a copy for offline review.");
            body.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
            body.getStyle().set("line-height", "1.5");
            card.add(body);
            root.add(card);
        }
        tabContent.add(root);
    }

    private void buildActivity() {
        VerticalLayout root = new VerticalLayout();
        root.setPadding(false);
        root.setSpacing(true);
        root.getStyle().set("padding", "20px 24px");

        record A(VaadinIcon ic, String text, String by, String when) {}
        List<A> items = List.of(
                new A(VaadinIcon.TRUCK,   "Shipped SHP-20247 to Milan, IT",                  "Jana Müller",   "2 hours ago"),
                new A(VaadinIcon.EDIT,    "Updated billing address",                         "Rosa Castillo", "Yesterday"),
                new A(VaadinIcon.CHECK,   "Approved May invoice (€14,820)",                  "System",        "2 days ago"),
                new A(VaadinIcon.INBOX,   "Sent monthly statement",                          "System",        "3 days ago"),
                new A(VaadinIcon.USER,    "Added new contact: Anna Brenner",                 "Adam Kowalski", "1 week ago"),
                new A(VaadinIcon.WARNING, "Shipment SHP-20198 delayed (Munich → Zurich)",    "System",        "1 week ago"));
        VerticalLayout timeline = new VerticalLayout();
        timeline.setPadding(false);
        timeline.setSpacing(true);
        timeline.getStyle()
                .set("padding-left", "18px")
                .set("border-left", "0.5px solid var(--lumo-contrast-10pct)");

        for (A a : items) {
            Icon icon = new Icon(a.ic);
            icon.setSize("10px");
            Span badge = new Span(icon);
            badge.getStyle()
                    .set("position", "absolute")
                    .set("left", "-27px").set("top", "2px")
                    .set("width", "18px").set("height", "18px")
                    .set("border-radius", "50%")
                    .set("background", "var(--lumo-base-color)")
                    .set("border", "0.5px solid var(--lumo-contrast-20pct)")
                    .set("display", "inline-flex").set("align-items", "center").set("justify-content", "center")
                    .set("color", "var(--lumo-secondary-text-color)");
            Span line = new Span(a.text);
            Span sub = new Span(a.by + " · " + a.when);
            sub.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);
            VerticalLayout text = new VerticalLayout(line, sub);
            text.setPadding(false);
            text.setSpacing(false);

            HorizontalLayout row = new HorizontalLayout(badge, text);
            row.getStyle().set("position", "relative");
            row.setSpacing(true);
            timeline.add(row);
        }
        root.add(timeline);
        tabContent.add(root);
    }

    private void buildBilling() {
        HorizontalLayout root = new HorizontalLayout();
        root.setWidthFull();
        root.setSpacing(true);
        root.getStyle().set("padding", "20px 24px");

        Card outstanding = new Card();
        outstanding.setTitle("Outstanding");
        Span s1 = new Span("Next invoice issues May 31");
        s1.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        outstanding.setSubtitle(s1);
        Span outValue = new Span("€14,820");
        outValue.getStyle().set("font-size", "28px").set("font-weight", "600").set("letter-spacing", "-0.02em");
        Span outMeta = new Span("3 unpaid invoices · oldest 12 days");
        outMeta.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        ProgressBar outBar = new ProgressBar(0, 100, 62);
        outBar.getStyle().set("--vaadin-progress-color", "var(--lumo-warning-color, #f59e0b)");
        VerticalLayout outBody = new VerticalLayout(outValue, outMeta, outBar);
        outBody.setPadding(false);
        outBody.setSpacing(true);
        outstanding.add(outBody);

        Card lifetime = new Card();
        lifetime.setTitle("Lifetime");
        Span s2 = new Span("Since 2019");
        s2.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        lifetime.setSubtitle(s2);
        Span lifeValue = new Span("€8.42M");
        lifeValue.getStyle().set("font-size", "28px").set("font-weight", "600").set("letter-spacing", "-0.02em");
        Span lifeMeta = new Span("Across 14,238 shipments");
        lifeMeta.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        ProgressBar lifeBar = new ProgressBar(0, 100, 88);
        lifeBar.getStyle().set("--vaadin-progress-color", "var(--lumo-success-color)");
        VerticalLayout lifeBody = new VerticalLayout(lifeValue, lifeMeta, lifeBar);
        lifeBody.setPadding(false);
        lifeBody.setSpacing(true);
        lifetime.add(lifeBody);

        outstanding.getStyle().set("flex", "1 1 0");
        lifetime.getStyle().set("flex", "1 1 0");
        root.add(outstanding, lifetime);
        tabContent.add(root);
    }

    // ── Form section helpers ───────────────────────────────────────────

    private VerticalLayout section(String title, String sub, String side, FormLayout form) {
        Span s1 = new Span(title);
        s1.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.SEMIBOLD);
        Span s2 = sub == null ? null : new Span(sub);
        if (s2 != null) s2.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        VerticalLayout meta = new VerticalLayout(s1);
        meta.setPadding(false);
        meta.setSpacing(false);
        if (s2 != null) meta.add(s2);

        HorizontalLayout head = new HorizontalLayout(meta);
        head.setWidthFull();
        head.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        head.setAlignItems(FlexComponent.Alignment.BASELINE);
        if (side != null) {
            Span sd = new Span(side);
            sd.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.TERTIARY);
            head.add(sd);
        }

        VerticalLayout section = new VerticalLayout(head, form);
        section.setPadding(false);
        section.setSpacing(true);
        section.getStyle()
                .set("padding", "22px 24px")
                .set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");
        return section;
    }

    private FormLayout form(Component... fields) {
        FormLayout f = new FormLayout(fields);
        return f;
    }

    private Component labeled(String label, Component input, String help) {
        if (input instanceof com.vaadin.flow.component.HasLabel hl) hl.setLabel(label);
        if (help != null && input instanceof com.vaadin.flow.component.HasHelper hh) hh.setHelperText(help);
        return input;
    }

    private Component labeledSpan(String label, Component input, String help) {
        Component c = labeled(label, input, help);
        // Span the field across both form columns
        if (c instanceof com.vaadin.flow.component.HasElement el) {
            el.getElement().setAttribute("colspan", "2");
        }
        return c;
    }

    private TextField textField(String value) {
        TextField t = new TextField();
        t.setValue(value);
        return t;
    }
    private TextField textFieldWithPrefix(String value, Icon prefix) {
        TextField t = new TextField();
        t.setValue(value);
        t.setPrefixComponent(prefix);
        return t;
    }
    private EmailField emailField(String value) {
        EmailField e = new EmailField();
        e.setValue(value);
        e.setPrefixComponent(new Icon(VaadinIcon.INBOX));
        return e;
    }

    private Select<String> industrySelect() {
        Select<String> s = new Select<>();
        Map<String, String> opts = new LinkedHashMap<>();
        opts.put("logistics", "Logistics & Freight");
        opts.put("retail",    "Retail & E-commerce");
        opts.put("manuf",     "Manufacturing");
        opts.put("pharma",    "Pharmaceutical");
        opts.put("auto",      "Automotive");
        s.setItems(opts.keySet());
        s.setItemLabelGenerator(opts::get);
        s.setValue("logistics");
        return s;
    }

    private IntegerField employeesField() {
        IntegerField f = new IntegerField();
        f.setValue(240);
        f.setStepButtonsVisible(true);
        f.setMin(0);
        f.setMax(99999);
        f.setStep(10);
        return f;
    }

    private Select<String> managerSelect() {
        Select<String> s = new Select<>();
        Map<String, String> opts = new LinkedHashMap<>();
        opts.put("rosa", "Rosa Castillo");
        opts.put("jana", "Jana Müller");
        opts.put("adam", "Adam Kowalski");
        opts.put("olu",  "Olu Kone");
        s.setItems(opts.keySet());
        s.setItemLabelGenerator(opts::get);
        s.setValue("rosa");
        return s;
    }

    private RadioButtonGroup<String> channelRadio() {
        RadioButtonGroup<String> g = new RadioButtonGroup<>();
        g.setItems("Email", "Phone", "SMS");
        g.setValue("Email");
        return g;
    }

    private RadioButtonGroup<String> windowRadio() {
        RadioButtonGroup<String> g = new RadioButtonGroup<>();
        g.setItems("Morning (08:00 – 12:00)", "Afternoon (12:00 – 17:00)", "Any time (next business day)");
        g.setValue("Morning (08:00 – 12:00)");
        return g;
    }

    private CheckboxGroup<String> channelsCheck() {
        CheckboxGroup<String> g = new CheckboxGroup<>();
        g.setItems("Email", "SMS", "Webhook", "Push");
        g.select("Email", "Webhook");
        return g;
    }

    private MultiSelectComboBox<String> handlingMulti() {
        MultiSelectComboBox<String> m = new MultiSelectComboBox<>();
        m.setItems("Hazardous", "Cold chain", "Fragile", "High value", "Oversize", "Lift-gate required");
        m.select("Hazardous", "Cold chain");
        m.setPlaceholder("Add handling tag…");
        return m;
    }

    private RadioButtonGroup<String> priorityRadio() {
        RadioButtonGroup<String> g = new RadioButtonGroup<>();
        g.setItems("Standard", "High", "Critical");
        g.setValue("Standard");
        return g;
    }

    private void toast(String title, String msg, NotificationVariant kind) {
        Notification n = new Notification();
        n.setPosition(Notification.Position.BOTTOM_END);
        n.setDuration(3000);
        n.addThemeVariants(kind);
        Span t = new Span(title);
        t.getStyle().set("font-weight", "600");
        VerticalLayout inner = new VerticalLayout(t);
        inner.setPadding(false);
        inner.setSpacing(false);
        if (msg != null && !msg.isEmpty()) {
            Span m = new Span(msg);
            m.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
            inner.add(m);
        }
        n.add(inner);
        n.open();
    }

    private static Span dot() {
        Span s = new Span("·");
        s.getStyle().set("opacity", "0.4");
        return s;
    }

    private static String statusKind(String status) {
        return switch (status) {
            case "Premium" -> "in-transit";
            case "Standard" -> "delivered";
            case "Trial" -> "pending";
            default -> "pending";
        };
    }
}
