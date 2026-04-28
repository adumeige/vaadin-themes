package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "terminal", layout = MainLayout.class)
@PageTitle("Terminal")
public class TerminalView extends VerticalLayout {

    public TerminalView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Terminal Interface"));

        // --- Login form ---
        add(new H3("Authentication"));
        var loginPanel = new Div();
        loginPanel.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "2px solid var(--lumo-primary-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("max-width", "360px")
                .set("box-shadow", "var(--lumo-box-shadow-l)");

        var loginTitle = new Span(">>> OPERATOR LOGIN <<<");
        loginTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "bold")
                .set("color", "var(--lumo-primary-color)")
                .set("text-transform", "uppercase")
                .set("display", "block")
                .set("text-align", "center")
                .set("margin-bottom", "var(--lumo-space-m)");

        var callsign = new TextField("Callsign");
        callsign.setPlaceholder("Enter callsign...");
        callsign.setWidthFull();

        var accessKey = new PasswordField("Access Key");
        accessKey.setPlaceholder("Enter key...");
        accessKey.setWidthFull();

        var loginBtn = new Button("AUTHENTICATE", VaadinIcon.LOCK.create());
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginBtn.setWidthFull();
        loginBtn.addClickListener(e ->
                Notification.show("AUTH OK — Welcome, operator", 3000, Notification.Position.BOTTOM_START));

        var loginLayout = new VerticalLayout(loginTitle, callsign, accessKey, loginBtn);
        loginLayout.setPadding(false);
        loginPanel.add(loginLayout);
        add(loginPanel);

        add(new Hr());

        // --- Command input ---
        add(new H3("Command Input"));
        var cmdPanel = new Div();
        cmdPanel.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-s)")
                .set("max-width", "480px");

        var cmdPrompt = new HorizontalLayout();
        cmdPrompt.setWidthFull();
        cmdPrompt.setSpacing(false);
        cmdPrompt.setAlignItems(Alignment.CENTER);
        cmdPrompt.getStyle().set("gap", "var(--lumo-space-s)");

        var prompt = new Span("> ");
        prompt.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("font-weight", "700")
                .set("flex-shrink", "0");

        var cmdInput = new TextField();
        cmdInput.setPlaceholder("type command...");
        cmdInput.setWidthFull();

        var escHint = new Span("ESC");
        escHint.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)")
                .set("padding", "2px 6px")
                .set("color", "var(--lumo-tertiary-text-color)")
                .set("flex-shrink", "0");

        cmdPrompt.add(prompt, cmdInput, escHint);
        cmdPanel.add(cmdPrompt);
        add(cmdPanel);

        add(new Hr());

        // --- Destructive actions ---
        add(new H3("Destructive Actions"));
        var dangerRow = new HorizontalLayout();
        dangerRow.setAlignItems(Alignment.CENTER);
        dangerRow.getStyle().set("gap", "var(--lumo-space-s)");

        var purgeBtn = new Button("PURGE BUFFER", VaadinIcon.TRASH.create());
        purgeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        purgeBtn.addClickListener(e -> {
            var confirm = new ConfirmDialog();
            confirm.setHeader("CONFIRM PURGE");
            confirm.setText("This will permanently erase all buffered signals. Type DELETE to confirm.");
            confirm.setCancelable(true);
            confirm.setConfirmText("PURGE");
            confirm.setConfirmButtonTheme("error primary");
            confirm.open();
        });

        var killBtn = new Button("KILL PROCESS", VaadinIcon.CLOSE_CIRCLE.create());
        killBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        killBtn.addClickListener(e -> {
            var n = Notification.show("Process terminated — PID 4821", 3000, Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        var resetBtn = new Button("FACTORY RESET");
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        dangerRow.add(purgeBtn, killBtn, resetBtn);
        add(dangerRow);

        add(new Hr());

        // --- System info panels ---
        add(new H3("System Information"));
        var sysRow = new FlexLayout();
        sysRow.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        sysRow.getStyle().set("gap", "var(--lumo-space-m)");

        sysRow.add(createInfoPanel("NODE", "ARCTIC-RELAY-07", "Region: Svalbard", "Uptime: 847d 14h"));
        sysRow.add(createInfoPanel("FIRMWARE", "v4.2.1-stable", "Built: 2026-04-12", "Signed: RSA-4096"));
        sysRow.add(createInfoPanel("NETWORK", "10.42.0.1/24", "Gateway: 10.42.0.254", "DNS: 10.42.0.2"));

        add(sysRow);

        add(new Hr());

        // --- Collapsible sections ---
        add(new H3("System Modules"));

        var rfModule = new Details("RF SUBSYSTEM",
                new VerticalLayout(
                        createDetailRow("Frequency range", "50 MHz — 1.3 GHz"),
                        createDetailRow("Active channels", "7 / 12"),
                        createDetailRow("Power output", "42 dBm"),
                        createDetailRow("Temperature", "38.2 °C")));
        rfModule.setOpened(true);

        var dspModule = new Details("DSP ENGINE",
                new VerticalLayout(
                        createDetailRow("Pipeline", "FFT → Demod → Decode"),
                        createDetailRow("Sample rate", "2.4 Msps"),
                        createDetailRow("CPU load", "67%"),
                        createDetailRow("Buffer", "1.2 GB / 4 GB")));

        var storModule = new Details("STORAGE ARRAY",
                new VerticalLayout(
                        createDetailRow("Total", "12 TB"),
                        createDetailRow("Used", "9.84 TB (82%)"),
                        createDetailRow("RAID", "6 (4+2)"),
                        createDetailRow("Health", "All disks OK")));

        add(rfModule, dspModule, storModule);
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.setPadding(false);
        row.getStyle()
                .set("border-bottom", "1px dashed var(--lumo-contrast-10pct)")
                .set("padding", "2px 0");

        var labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "var(--lumo-secondary-text-color)");

        var valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("font-variant-numeric", "tabular-nums");

        row.add(labelSpan, valueSpan);
        return row;
    }

    private Div createInfoPanel(String title, String value, String line1, String line2) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("min-width", "220px")
                .set("flex", "1")
                .set("overflow", "hidden");

        var header = new Div();
        header.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-xs) var(--lumo-space-s)")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.12em")
                .set("color", "var(--lumo-secondary-text-color)");
        header.setText(title);

        var body = new VerticalLayout();
        body.setPadding(true);
        body.setSpacing(false);
        body.getStyle().set("gap", "var(--lumo-space-xs)");

        var valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "bold")
                .set("color", "var(--lumo-primary-color)");

        var l1 = new Span(line1);
        l1.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-tertiary-text-color)");
        var l2 = new Span(line2);
        l2.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-tertiary-text-color)");

        body.add(valueSpan, l1, l2);
        card.add(header, body);
        return card;
    }
}
