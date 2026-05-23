package org.antoined.vaadinthemes.testapp.views.glass;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.antoined.vaadinthemes.testapp.views.MainLayout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Route(value = "glass/settings", layout = MainLayout.class)
@PageTitle("Settings — Glass")
public class SettingsView extends VerticalLayout {

    private record Notif(String label, String sub, boolean email, boolean push, boolean sms) {}
    private record Integration(String id, String name, String desc, boolean connected) {}
    private record Session(String device, String location, String when, boolean current) {}
    private record ApiKey(String name, String fingerprint, String lastUsed) {}

    public SettingsView() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        getStyle().set("overflow", "auto");

        VerticalLayout page = new VerticalLayout();
        page.setPadding(false);
        page.setSpacing(false);
        page.setSizeFull();

        page.add(buildPageHead());

        // Body — vertically stacked Cards in an 880px column.
        VerticalLayout body = new VerticalLayout(
                profileCard(),
                notificationsCard(),
                securityCard(),
                integrationsCard(),
                advancedCard(),
                dangerCard()
        );
        body.setPadding(true);
        body.setSpacing(true);
        body.setMaxWidth("880px");
        body.setWidthFull();
        body.getStyle().set("margin", "0 auto").set("padding", "22px 28px 28px");
        page.add(body);

        add(page);
    }

    private Component buildPageHead() {
        H1 title = new H1("Settings");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.SEMIBOLD);
        title.getStyle().set("letter-spacing", "-0.02em");
        Span sub = new Span("Account, security, notifications, integrations");
        sub.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.SECONDARY);
        VerticalLayout meta = new VerticalLayout(title, sub);
        meta.setPadding(false);
        meta.setSpacing(false);
        meta.getStyle().set("gap", "6px");

        HorizontalLayout head = new HorizontalLayout(meta);
        head.setWidthFull();
        head.setAlignItems(FlexComponent.Alignment.END);
        head.getStyle()
                .set("padding", "24px 28px 18px")
                .set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)")
                .set("flex", "0 0 auto");
        return head;
    }

    // ── Profile ─────────────────────────────────────────────────────────

    private Card profileCard() {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Profile");

        // Header row: avatar + identity + actions
        Avatar avatar = new Avatar("Jana Müller", "JM");
        avatar.getStyle().set("width", "64px").set("height", "64px").set("font-size", "20px");

        Span name = new Span("Jana Müller");
        name.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.MEDIUM);
        Span role = new Span("Dispatch Manager · Berlin office");
        role.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        VerticalLayout id = new VerticalLayout(name, role);
        id.setPadding(false);
        id.setSpacing(false);

        Button changePhoto = new Button("Change photo", new Icon(VaadinIcon.UPLOAD));
        changePhoto.addThemeVariants(ButtonVariant.LUMO_SMALL);
        Button rm = new Button(new Icon(VaadinIcon.TRASH));
        rm.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

        HorizontalLayout topRow = new HorizontalLayout(avatar, id, changePhoto, rm);
        topRow.setWidthFull();
        topRow.setAlignItems(FlexComponent.Alignment.CENTER);
        topRow.expand(id);

        // Form fields
        TextField fullName = new TextField("Full name");
        fullName.setValue("Jana Müller");

        EmailField email = new EmailField("Email");
        email.setValue("jana@northwind.co");
        email.setPrefixComponent(new Icon(VaadinIcon.INBOX));

        Select<String> lang = new Select<>();
        lang.setLabel("Language");
        Map<String, String> langs = new LinkedHashMap<>();
        langs.put("en", "English (US)"); langs.put("de", "Deutsch");
        langs.put("fr", "Français");     langs.put("es", "Español");
        lang.setItems(langs.keySet());
        lang.setItemLabelGenerator(langs::get);
        lang.setValue("en");

        Select<String> tz = new Select<>();
        tz.setLabel("Timezone");
        Map<String, String> tzs = new LinkedHashMap<>();
        tzs.put("berlin", "Europe/Berlin (UTC+1)");
        tzs.put("london", "Europe/London (UTC+0)");
        tzs.put("ny",     "America/New_York (UTC-5)");
        tz.setItems(tzs.keySet());
        tz.setItemLabelGenerator(tzs::get);
        tz.setValue("berlin");

        RadioButtonGroup<String> appearance = new RadioButtonGroup<>();
        appearance.setLabel("Appearance");
        appearance.setItems("Light", "Dark", "Match system");
        appearance.setValue("Match system");

        FormLayout form = new FormLayout(fullName, email, lang, tz, appearance);
        form.setColspan(appearance, 2);

        VerticalLayout body = new VerticalLayout(topRow, form);
        body.setPadding(false);
        body.setSpacing(true);
        card.add(body);
        return card;
    }

    // ── Notifications matrix ────────────────────────────────────────────

    private Card notificationsCard() {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Notifications");
        Span sub = new Span("Choose how you'd like to be reached.");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);

        Grid<Notif> grid = new Grid<>(Notif.class, false);
        grid.setAllRowsVisible(true);
        grid.setItems(
                new Notif("Critical incidents", "Delays, exceptions, cancellations", true,  true,  true),
                new Notif("Daily digest",       "Summary of shipments and revenue",  true,  false, false),
                new Notif("Weekly report",      "Performance and KPIs vs last week", true,  false, false),
                new Notif("Status updates",     "Every shipment status change",      false, false, false),
                new Notif("Mentions & comments","When someone @mentions you",        true,  true,  false));

        grid.addColumn(new ComponentRenderer<>(n -> {
            Span lbl = new Span(n.label());
            lbl.addClassNames(LumoUtility.FontWeight.MEDIUM);
            Span sb = new Span(n.sub());
            sb.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);
            VerticalLayout v = new VerticalLayout(lbl, sb);
            v.setPadding(false);
            v.setSpacing(false);
            return v;
        })).setHeader("Event").setFlexGrow(1);

        grid.addComponentColumn(n -> centeredCheckbox(n.email())).setHeader("Email").setAutoWidth(true).setFlexGrow(0).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        grid.addComponentColumn(n -> centeredCheckbox(n.push())) .setHeader("Push") .setAutoWidth(true).setFlexGrow(0).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        grid.addComponentColumn(n -> centeredCheckbox(n.sms()))  .setHeader("SMS")  .setAutoWidth(true).setFlexGrow(0).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

        card.add(grid);
        return card;
    }

    private Checkbox centeredCheckbox(boolean checked) {
        Checkbox c = new Checkbox(checked);
        return c;
    }

    // ── Security ────────────────────────────────────────────────────────

    private Card securityCard() {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Security");
        Span sub = new Span("Password, two-factor, sessions.");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);

        PasswordField current = new PasswordField("Current password");
        current.setPlaceholder("••••••••");
        PasswordField next = new PasswordField("New password");
        next.setHelperText("At least 8 characters");

        FormLayout pwForm = new FormLayout(current, next);

        // 2FA row
        Span twoFaLabel = new Span("Two-factor authentication");
        twoFaLabel.addClassNames(LumoUtility.FontWeight.MEDIUM);
        Span twoFaDesc = new Span("Require a code from your authenticator app on sign-in.");
        twoFaDesc.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        VerticalLayout twoFaText = new VerticalLayout(twoFaLabel, twoFaDesc);
        twoFaText.setPadding(false);
        twoFaText.setSpacing(false);

        Checkbox twoFaToggle = new Checkbox(true);
        HorizontalLayout twoFaRow = new HorizontalLayout(twoFaText, twoFaToggle);
        twoFaRow.setWidthFull();
        twoFaRow.setAlignItems(FlexComponent.Alignment.CENTER);
        twoFaRow.expand(twoFaText);
        twoFaRow.getStyle().set("padding-top", "14px").set("border-top", "0.5px solid var(--lumo-contrast-10pct)");

        // Active sessions
        Span sessHead = new Span("Active sessions");
        sessHead.addClassNames(LumoUtility.FontWeight.MEDIUM);
        VerticalLayout sessions = new VerticalLayout(
                sessHead,
                sessionRow(new Session("MacBook Pro · Safari",   "Berlin, DE", "Active now",  true)),
                sessionRow(new Session("iPhone 15 · Vaadin App", "Berlin, DE", "3 hours ago", false)),
                sessionRow(new Session("Chrome · Windows",       "Munich, DE", "2 days ago",  false))
        );
        sessions.setPadding(false);
        sessions.setSpacing(true);
        sessions.getStyle().set("padding-top", "14px").set("border-top", "0.5px solid var(--lumo-contrast-10pct)");

        VerticalLayout body = new VerticalLayout(pwForm, twoFaRow, sessions);
        body.setPadding(false);
        body.setSpacing(true);
        card.add(body);
        return card;
    }

    private Component sessionRow(Session s) {
        Avatar a = new Avatar(s.device());
        Span device = new Span(s.device());
        device.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.MEDIUM);

        HorizontalLayout deviceLine = new HorizontalLayout(device);
        if (s.current()) {
            Span tag = new Span("This device");
            tag.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.SUCCESS);
            deviceLine.add(tag);
        }
        deviceLine.setSpacing(true);
        deviceLine.setAlignItems(FlexComponent.Alignment.BASELINE);

        Span sub = new Span(s.location() + " · " + s.when());
        sub.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);

        VerticalLayout text = new VerticalLayout(deviceLine, sub);
        text.setPadding(false);
        text.setSpacing(false);

        HorizontalLayout row = new HorizontalLayout(a, text);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.expand(text);
        row.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.BorderRadius.SMALL);
        row.getStyle()
                .set("border", "0.5px solid var(--lumo-contrast-10pct)")
                .set("background", "var(--lumo-base-color)");

        if (!s.current()) {
            Button signOut = new Button("Sign out");
            signOut.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            row.add(signOut);
        }
        return row;
    }

    // ── Integrations ────────────────────────────────────────────────────

    private Card integrationsCard() {
        List<Integration> integrations = List.of(
                new Integration("slack",   "Slack",   "Post dispatch alerts to a channel.",    true),
                new Integration("sap",     "SAP",     "Sync customer + invoice records.",      true),
                new Integration("shopify", "Shopify", "Fulfill orders directly from Shopify.", false),
                new Integration("stripe",  "Stripe",  "Auto-reconcile incoming payments.",     true),
                new Integration("hubspot", "HubSpot", "Mirror accounts to your CRM.",          false));
        long connected = integrations.stream().filter(Integration::connected).count();

        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Integrations");
        Span sub = new Span(connected + " connected");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(true);
        for (Integration it : integrations) {
            list.add(integrationRow(it));
        }
        card.add(list);
        return card;
    }

    private Component integrationRow(Integration it) {
        Span letter = new Span(String.valueOf(it.name().charAt(0)));
        letter.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SECONDARY);
        letter.getStyle()
                .set("width", "38px").set("height", "38px").set("border-radius", "9px")
                .set("display", "inline-flex").set("align-items", "center").set("justify-content", "center")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "0.5px solid var(--lumo-contrast-10pct)");

        Span name = new Span(it.name());
        name.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        Span desc = new Span(it.desc());
        desc.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        VerticalLayout text = new VerticalLayout(name, desc);
        text.setPadding(false);
        text.setSpacing(false);

        HorizontalLayout row = new HorizontalLayout(letter, text);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.expand(text);

        if (it.connected()) {
            Span pill = new Span("Connected");
            pill.setClassName("vg-status vg-status-delivered");
            row.add(pill);
        } else {
            Button btn = new Button("Connect");
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            row.add(btn);
        }
        return row;
    }

    // ── Advanced ────────────────────────────────────────────────────────

    private Card advancedCard() {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Advanced");

        VerticalLayout list = new VerticalLayout(
                detail("API access", "2 keys",
                        keyRow(new ApiKey("Production key", "vk_…7c2a", "2 min ago")),
                        keyRow(new ApiKey("Staging key",    "vk_…f019", "Yesterday")),
                        genKeyButton()),
                detail("Webhooks",      "3 endpoints", secondaryText("Configure HTTP endpoints to receive shipment events.")),
                detail("Audit log",     "Last 90 days", secondaryText("Download a CSV of every action taken on this account.")),
                detail("Data residency","EU",           secondaryText("All data is stored in Frankfurt (eu-central-1) per GDPR requirements."))
        );
        list.setPadding(false);
        list.setSpacing(false);
        card.add(list);
        return card;
    }

    private Span secondaryText(String text) {
        Span s = new Span(text);
        s.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        return s;
    }

    private Button genKeyButton() {
        Button b = new Button("Generate new key", new Icon(VaadinIcon.PLUS));
        b.addThemeVariants(ButtonVariant.LUMO_SMALL);
        return b;
    }

    private Details detail(String title, String sub, Component... body) {
        Span s1 = new Span(title);
        s1.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        Span s2 = new Span(" · " + sub);
        s2.addClassNames(LumoUtility.TextColor.SECONDARY);
        Details d = new Details(new HorizontalLayout(s1, s2));
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        for (Component c : body) content.add(c);
        d.add(content);
        d.getStyle().set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");
        return d;
    }

    private Component keyRow(ApiKey k) {
        Span name = new Span(k.name());
        name.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.MEDIUM);
        Span sub = new Span(k.fingerprint() + " · last used " + k.lastUsed());
        sub.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY);
        sub.getStyle().set("font-family", "var(--vg-font-mono, ui-monospace)");
        VerticalLayout text = new VerticalLayout(name, sub);
        text.setPadding(false);
        text.setSpacing(false);

        Button copy  = new Button(new Icon(VaadinIcon.COPY));
        copy.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
        Button trash = new Button(new Icon(VaadinIcon.TRASH));
        trash.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);

        HorizontalLayout row = new HorizontalLayout(text, copy, trash);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.expand(text);
        row.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.BorderRadius.SMALL);
        row.getStyle()
                .set("border", "0.5px solid var(--lumo-contrast-10pct)")
                .set("background", "var(--lumo-base-color)");
        return row;
    }

    // ── Danger zone ─────────────────────────────────────────────────────

    private Card dangerCard() {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Danger zone");

        Span label = new Span("Delete this workspace");
        label.addClassNames(LumoUtility.FontWeight.MEDIUM, LumoUtility.FontSize.MEDIUM);
        Span sub = new Span("Permanently delete all data. This cannot be undone.");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        VerticalLayout text = new VerticalLayout(label, sub);
        text.setPadding(false);
        text.setSpacing(false);

        Button del = new Button("Delete workspace", new Icon(VaadinIcon.TRASH));
        del.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        del.addClickListener(e -> {
            ConfirmDialog cd = new ConfirmDialog();
            cd.setHeader("Delete this workspace?");
            cd.setText("All shipments, customers, and integration data will be permanently removed. " +
                    "Type DELETE to confirm — this cannot be undone.");
            cd.setCancelable(true);
            cd.setConfirmText("Delete workspace");
            cd.setConfirmButtonTheme("primary error");
            cd.addConfirmListener(ev -> {
                Notification n = Notification.show("Deletion cancelled — demo doesn't actually delete anything.");
                n.addThemeVariants(NotificationVariant.LUMO_WARNING);
                n.setPosition(Notification.Position.BOTTOM_END);
            });
            cd.open();
        });

        HorizontalLayout row = new HorizontalLayout(text, del);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.expand(text);
        card.add(row);
        return card;
    }
}
