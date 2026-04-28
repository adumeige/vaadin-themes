package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "signal-dashboard", layout = MainLayout.class)
@PageTitle("Signal Dashboard")
public class SignalDashboardView extends VerticalLayout {

    public record Signal(int idx, String callsign, String freq, double strength,
                         String status, String latency, String protocol) {}

    public SignalDashboardView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Signal Console"));

        // --- KPI cards ---
        var kpiRow = new FlexLayout();
        kpiRow.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        kpiRow.getStyle().set("gap", "var(--lumo-space-m)");

        kpiRow.add(createKpiCard("ACTIVE SIGNALS", "2,847", "var(--lumo-success-color)"));
        kpiRow.add(createKpiCard("THROUGHPUT", "1.42 Gbps", "var(--lumo-primary-color)"));
        kpiRow.add(createKpiCard("LATENCY AVG", "12.4 ms", "var(--lumo-primary-text-color)"));
        kpiRow.add(createKpiCard("ERROR RATE", "0.03%", "var(--lumo-error-color)"));

        add(kpiRow);
        add(new Hr());

        // --- Signal grid ---
        add(new H3("Signal Registry"));
        var grid = new Grid<>(Signal.class, false);

        grid.addColumn(s -> String.format("%03d", s.idx())).setHeader("#").setWidth("60px").setFlexGrow(0);
        grid.addColumn(Signal::callsign).setHeader("Callsign").setSortable(true);
        grid.addColumn(Signal::freq).setHeader("Freq").setSortable(true);
        grid.addColumn(new ComponentRenderer<>(signal -> {
            var bar = new ProgressBar();
            bar.setValue(signal.strength());
            bar.setWidth("100px");
            bar.setHeight("10px");
            return bar;
        })).setHeader("Strength").setWidth("130px");
        grid.addColumn(new ComponentRenderer<>(signal -> {
            var badge = new Span(signal.status());
            String theme = switch (signal.status()) {
                case "ONLINE" -> "badge success";
                case "DEGRADED" -> "badge error";
                case "STANDBY" -> "badge contrast";
                default -> "badge";
            };
            badge.getElement().getThemeList().add(theme);
            return badge;
        })).setHeader("Status");
        grid.addColumn(Signal::latency).setHeader("Latency");
        grid.addColumn(Signal::protocol).setHeader("Protocol");

        grid.setItems(
                new Signal(1, "ARCTIC-7", "144.800 MHz", 0.92, "ONLINE", "4.2 ms", "AX.25"),
                new Signal(2, "BOREAL-3", "432.100 MHz", 0.78, "ONLINE", "8.1 ms", "DMR"),
                new Signal(3, "CIPHER-X", "1296.0 MHz", 0.45, "DEGRADED", "34.7 ms", "D-STAR"),
                new Signal(4, "DELTA-9", "50.125 MHz", 0.88, "ONLINE", "6.3 ms", "FT8"),
                new Signal(5, "ECHO-11", "28.410 MHz", 0.31, "STANDBY", "---", "SSB"),
                new Signal(6, "FROST-2", "222.100 MHz", 0.67, "ONLINE", "11.2 ms", "C4FM"),
                new Signal(7, "GHOST-4", "902.0 MHz", 0.95, "ONLINE", "2.8 ms", "APRS"),
                new Signal(8, "HELIX-6", "70.200 MHz", 0.12, "DEGRADED", "82.4 ms", "RTTY"),
                new Signal(9, "IRIS-15", "446.0 MHz", 0.53, "ONLINE", "15.0 ms", "PMR446"),
                new Signal(10, "JADE-1", "1240.0 MHz", 0.81, "STANDBY", "---", "ATV")
        );
        grid.setHeight("400px");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        add(grid);

        add(new Hr());

        // --- System status panels ---
        add(new H3("System Status"));
        var statusRow = new FlexLayout();
        statusRow.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        statusRow.getStyle().set("gap", "var(--lumo-space-m)");

        statusRow.add(createStatusPanel("UPLINK", "NOMINAL", true, 0.94));
        statusRow.add(createStatusPanel("DOWNLINK", "NOMINAL", true, 0.87));
        statusRow.add(createStatusPanel("DECODER", "DEGRADED", false, 0.45));
        statusRow.add(createStatusPanel("STORAGE", "82% USED", true, 0.82));

        add(statusRow);
        add(new Hr());

        // --- Event log ---
        add(new H3("Event Log"));
        var log = new VerticalLayout();
        log.setPadding(true);
        log.setSpacing(false);
        log.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("font-family", "var(--lumo-font-family)")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("max-height", "200px")
                .set("overflow-y", "auto");

        addLogEntry(log, "14:32:07", "INFO", "Signal ARCTIC-7 locked — SNR 24.3 dB");
        addLogEntry(log, "14:32:05", "WARN", "CIPHER-X packet loss exceeding threshold (12.4%)");
        addLogEntry(log, "14:32:03", "INFO", "Decoder pipeline restarted on channel 3");
        addLogEntry(log, "14:32:01", "ERR", "HELIX-6 sync lost — attempting re-acquisition");
        addLogEntry(log, "14:31:58", "INFO", "GHOST-4 handoff complete — new beam: NE-42");
        addLogEntry(log, "14:31:55", "INFO", "Throughput report: 1.42 Gbps aggregate");
        addLogEntry(log, "14:31:52", "WARN", "Storage pool B approaching capacity (82%)");
        addLogEntry(log, "14:31:50", "INFO", "BOREAL-3 frequency hop — 432.100 -> 432.200 MHz");
        addLogEntry(log, "14:31:47", "INFO", "Scheduled calibration pass on FROST-2");
        addLogEntry(log, "14:31:44", "ERR", "Authentication timeout on CIPHER-X control channel");

        add(log);
    }

    private void addLogEntry(VerticalLayout log, String time, String level, String message) {
        var row = new HorizontalLayout();
        row.setSpacing(false);
        row.getStyle()
                .set("gap", "var(--lumo-space-s)")
                .set("padding", "2px 0")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("width", "100%");

        var timeSpan = new Span(time);
        timeSpan.getStyle()
                .set("color", "var(--lumo-tertiary-text-color)")
                .set("flex-shrink", "0")
                .set("font-variant-numeric", "tabular-nums");

        var levelSpan = new Span("[" + level + "]");
        levelSpan.getStyle().set("flex-shrink", "0").set("width", "50px");
        String levelColor = switch (level) {
            case "ERR" -> "var(--lumo-error-color)";
            case "WARN" -> "var(--lumo-warning-color, var(--lumo-error-color))";
            default -> "var(--lumo-primary-color)";
        };
        levelSpan.getStyle().set("color", levelColor);

        var msgSpan = new Span(message);
        msgSpan.getStyle().set("color", "var(--lumo-body-text-color)");

        row.add(timeSpan, levelSpan, msgSpan);
        log.add(row);
    }

    private Div createKpiCard(String label, String value, String color) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m)")
                .set("min-width", "180px")
                .set("flex", "1")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");

        var labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("display", "block");

        var valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xxl)")
                .set("font-weight", "bold")
                .set("color", color)
                .set("display", "block")
                .set("margin-top", "var(--lumo-space-xs)");

        var layout = new VerticalLayout(labelSpan, valueSpan);
        layout.setPadding(false);
        layout.setSpacing(false);
        card.add(layout);
        return card;
    }

    private Div createStatusPanel(String name, String status, boolean ok, double value) {
        var card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-s)")
                .set("min-width", "180px")
                .set("flex", "1");

        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        var nameSpan = new Span(name);
        nameSpan.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em")
                .set("color", "var(--lumo-secondary-text-color)");

        var statusBadge = new Span(status);
        statusBadge.getElement().getThemeList().add(ok ? "badge success" : "badge error");

        header.add(nameSpan, statusBadge);

        var bar = new ProgressBar();
        bar.setValue(value);
        bar.setWidthFull();
        bar.getStyle().set("margin-top", "var(--lumo-space-xs)");

        var layout = new VerticalLayout(header, bar);
        layout.setPadding(false);
        layout.setSpacing(false);
        card.add(layout);
        return card;
    }
}
