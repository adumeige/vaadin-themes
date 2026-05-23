package org.antoined.vaadinthemes.testapp.views.glass;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.antoined.vaadinthemes.testapp.views.MainLayout;

import java.util.List;

@Route(value = "glass/insights", layout = MainLayout.class)
@PageTitle("Insights — Glass")
public class InsightsView extends VerticalLayout {

    private record Cust(String id, String name, int revenue, String initials, int hue) {}
    private static final List<Cust> TOP_CUST = List.of(
            new Cust("acme",    "Acme Industrial",   184500, "AI", 215),
            new Cust("northw",  "Northwind Traders", 142800, "NT", 145),
            new Cust("vertex",  "Vertex Logistics",   98200, "VL", 280),
            new Cust("globex",  "Globex Corporation", 67400, "GC",  25),
            new Cust("initech", "Initech Systems",    58900, "IS", 195)
    );

    public InsightsView() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        getStyle().set("overflow", "auto");

        add(buildPageHead());

        VerticalLayout body = new VerticalLayout(
                kpisRow(),
                cardRow("2fr 1fr", revenueChart(), shipmentDonut()),
                cardRow("2fr 1fr", regionBars(),   topCustomers()),
                carrierUtilization()
        );
        body.setPadding(true);
        body.setSpacing(true);
        body.setWidthFull();
        body.getStyle().set("padding", "22px 28px 28px");
        add(body);
    }

    private Component buildPageHead() {
        H1 title = new H1("Insights");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.SEMIBOLD);
        title.getStyle().set("letter-spacing", "-0.02em");
        Span sub = new Span("Performance and revenue across all customers · last sync 4 min ago");
        sub.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.SECONDARY);
        VerticalLayout meta = new VerticalLayout(title, sub);
        meta.setPadding(false);
        meta.setSpacing(false);
        meta.getStyle().set("gap", "6px");

        Tabs range = new Tabs(new Tab("7D"), new Tab("30D"), new Tab("90D"), new Tab("12M"));
        range.setSelectedIndex(1);
        range.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        Button dateBtn = new Button("Apr 22 – May 22", new Icon(VaadinIcon.CALENDAR));
        dateBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        Button exportBtn = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        HorizontalLayout actions = new HorizontalLayout(range, dateBtn, exportBtn);
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

    // ── KPI row ────────────────────────────────────────────────────────

    private HorizontalLayout kpisRow() {
        HorizontalLayout row = new HorizontalLayout(
                kpi(VaadinIcon.BAR_CHART_V, "Revenue",   "€512k",  "+14.2%", true,  "vs prior 30d"),
                kpi(VaadinIcon.TRUCK,       "Shipments", "2,418",  "+8.4%",  true,  "vs prior 30d"),
                kpi(VaadinIcon.MAGIC,       "Avg margin","23.4%",  "-0.8pp", false, "vs prior 30d"),
                kpi(VaadinIcon.USERS,       "NPS",       "72",     "+4",     true,  "vs prior 30d"));
        row.setWidthFull();
        row.setSpacing(true);
        for (int i = 0; i < row.getComponentCount(); i++) {
            ((Card) row.getComponentAt(i)).getStyle().set("flex", "1 1 0");
        }
        ((Card) row.getComponentAt(0)).getElement().getThemeList().add("glass");
        return row;
    }

    private Card kpi(VaadinIcon icon, String label, String value, String trend, boolean up, String suffix) {
        Card card = new Card();
        card.setWidthFull();

        Icon ic = new Icon(icon);
        ic.setSize("14px");
        Span lblText = new Span(label);
        lblText.addClassNames(LumoUtility.FontWeight.MEDIUM, LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        HorizontalLayout lbl = new HorizontalLayout(ic, lblText);
        lbl.setSpacing(true);
        lbl.setAlignItems(FlexComponent.Alignment.CENTER);

        Span val = new Span(value);
        val.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        val.getStyle().set("font-size", "28px").set("letter-spacing", "-0.025em").set("font-variant-numeric", "tabular-nums");

        Span pill = new Span();
        Icon ti = new Icon(up ? VaadinIcon.ARROW_UP : VaadinIcon.ARROW_DOWN);
        ti.setSize("11px");
        pill.add(ti, new Span(trend));
        pill.setClassName("vg-trend " + (up ? "vg-trend-up" : "vg-trend-down"));
        Span suf = new Span(suffix);
        suf.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        HorizontalLayout meta = new HorizontalLayout(pill, suf);
        meta.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout body = new VerticalLayout(lbl, val, meta);
        body.setPadding(false);
        body.setSpacing(false);
        body.getStyle().set("gap", "6px");
        card.add(body);
        return card;
    }

    // ── Card row helper ────────────────────────────────────────────────

    private HorizontalLayout cardRow(String columns, Component... children) {
        HorizontalLayout row = new HorizontalLayout(children);
        row.setWidthFull();
        row.setSpacing(true);
        // Apply flex ratios in order: "2fr 1fr" → ["2", "1"]
        String[] parts = columns.split("\\s+");
        for (int i = 0; i < children.length && i < parts.length; i++) {
            String p = parts[i].replace("fr", "");
            if (children[i] instanceof com.vaadin.flow.component.HasStyle hs) {
                hs.getStyle().set("flex", p + " 1 0").set("min-width", "0");
            }
        }
        return row;
    }

    // ── Charts ─────────────────────────────────────────────────────────

    private Card revenueChart() {
        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Revenue vs target");
        Span sub = new Span("Daily, last 30 days");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);

        Html legend = new Html("<div style='display:flex;gap:14px;font-size:12px;color:var(--lumo-secondary-text-color);align-items:center'>" +
                "<span style='display:inline-flex;align-items:center;gap:6px'>" +
                "<i style='width:8px;height:8px;border-radius:2px;background:var(--lumo-primary-color);display:inline-block'></i>Actual</span>" +
                "<span style='display:inline-flex;align-items:center;gap:6px'>" +
                "<i style='width:8px;height:8px;border-radius:2px;background:var(--lumo-tertiary-text-color);display:inline-block;opacity:0.6'></i>Target</span>" +
                "</div>");
        card.setHeaderSuffix(legend);

        double[] actual = new double[30];
        double[] target = new double[30];
        for (int i = 0; i < 30; i++) {
            actual[i] = 80 + Math.sin(i / 3.0) * 18 + i * 1.6 + (i % 7 == 0 ? -10 : 0);
            target[i] = 90 + i * 1.5;
        }
        card.add(new Html("<div style='width:100%'>" + lineSvg(actual, target) + "</div>"));
        return card;
    }

    private String lineSvg(double[] actual, double[] target) {
        double max = 0;
        for (double v : actual) max = Math.max(max, v);
        for (double v : target) max = Math.max(max, v);
        max *= 1.1;
        int w = 100, h = 36;
        StringBuilder pathA = new StringBuilder();
        StringBuilder pathT = new StringBuilder();
        for (int i = 0; i < actual.length; i++) {
            double x = (i / (double) (actual.length - 1)) * w;
            double yA = h - (actual[i] / max) * h;
            double yT = h - (target[i] / max) * h;
            pathA.append(i == 0 ? "M" : "L").append(String.format(java.util.Locale.ROOT, "%.2f %.2f ", x, yA));
            pathT.append(i == 0 ? "M" : "L").append(String.format(java.util.Locale.ROOT, "%.2f %.2f ", x, yT));
        }
        return "<svg viewBox='0 0 " + w + " " + (h + 1) + "' preserveAspectRatio='none' style='width:100%;height:200px;display:block'>" +
                "<defs><linearGradient id='vg-area' x1='0' y1='0' x2='0' y2='1'>" +
                "<stop offset='0' stop-color='var(--lumo-primary-color)' stop-opacity='0.35'/>" +
                "<stop offset='1' stop-color='var(--lumo-primary-color)' stop-opacity='0'/>" +
                "</linearGradient></defs>" +
                "<path d='" + pathA + " L " + w + " " + h + " L 0 " + h + " Z' fill='url(#vg-area)' />" +
                "<path d='" + pathT + "' fill='none' stroke='var(--lumo-tertiary-text-color)' stroke-width='0.6' stroke-dasharray='1.2 1.2' opacity='0.7'/>" +
                "<path d='" + pathA + "' fill='none' stroke='var(--lumo-primary-color)' stroke-width='1.2' stroke-linejoin='round' stroke-linecap='round'/>" +
                "</svg>";
    }

    private Card shipmentDonut() {
        record Seg(String label, int value, String color) {}
        List<Seg> segs = List.of(
                new Seg("Delivered",   412, "var(--lumo-success-color)"),
                new Seg("In transit",  184, "var(--lumo-primary-color)"),
                new Seg("Delayed",      22, "var(--lumo-warning-color, #f59e0b)"),
                new Seg("Cancelled",     8, "var(--lumo-error-color)"));
        int total = segs.stream().mapToInt(Seg::value).sum();

        StringBuilder svg = new StringBuilder();
        double cx = 60, cy = 60, r = 48, sw = 14;
        svg.append("<svg viewBox='0 0 120 120' style='width:140px;height:140px;display:block'>");
        double cum = 0;
        for (Seg s : segs) {
            double frac = s.value / (double) total;
            double start = cum * 2 * Math.PI - Math.PI / 2;
            double end = (cum + frac) * 2 * Math.PI - Math.PI / 2;
            double x1 = cx + r * Math.cos(start);
            double y1 = cy + r * Math.sin(start);
            double x2 = cx + r * Math.cos(end);
            double y2 = cy + r * Math.sin(end);
            int large = frac > 0.5 ? 1 : 0;
            svg.append(String.format(java.util.Locale.ROOT,
                    "<path d='M %.2f %.2f A %.2f %.2f 0 %d 1 %.2f %.2f' fill='none' stroke='%s' stroke-width='%.1f' stroke-linecap='butt'/>",
                    x1, y1, r, r, large, x2, y2, s.color, sw));
            cum += frac;
        }
        svg.append("<text x='60' y='58' text-anchor='middle' font-size='22' font-weight='600' fill='var(--lumo-body-text-color)'>" + total + "</text>");
        svg.append("<text x='60' y='74' text-anchor='middle' font-size='9' fill='var(--lumo-tertiary-text-color)'>total</text>");
        svg.append("</svg>");

        StringBuilder legHtml = new StringBuilder();
        legHtml.append("<div style='display:grid;grid-template-columns:1fr 1fr;row-gap:8px;font-size:12px;color:var(--lumo-secondary-text-color)'>");
        for (Seg s : segs) {
            int pct = (int) Math.round((s.value / (double) total) * 100);
            legHtml.append("<span style='display:inline-flex;align-items:center;gap:6px'>")
                    .append("<i style='width:8px;height:8px;border-radius:2px;background:").append(s.color).append(";display:inline-block'></i>")
                    .append(s.label).append(" <span style='color:var(--lumo-tertiary-text-color);margin-left:auto'>")
                    .append(pct).append("%</span></span>");
        }
        legHtml.append("</div>");

        VerticalLayout body = new VerticalLayout(
                new Html("<div>" + svg + "</div>"),
                new Html(legHtml.toString()));
        body.setPadding(false);
        body.setSpacing(true);
        body.setAlignItems(FlexComponent.Alignment.STRETCH);
        body.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, body.getComponentAt(0));

        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Shipment status");
        Span sub = new Span("Last 30 days");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);
        card.add(body);
        return card;
    }

    private Card regionBars() {
        int[] data = {142, 96, 78, 64, 51, 32};
        String[] labels = {"DE", "NL", "FR", "IT", "ES", "PL"};
        int max = 0;
        for (int v : data) max = Math.max(max, v);

        StringBuilder svg = new StringBuilder();
        svg.append("<svg viewBox='0 0 600 220' style='width:100%;height:220px;display:block'>");
        int n = data.length;
        double bw = 60, gap = 28, x0 = 20;
        for (int i = 0; i < n; i++) {
            double h = (data[i] / (double) max) * 170;
            double x = x0 + i * (bw + gap);
            double y = 200 - h;
            svg.append(String.format(java.util.Locale.ROOT,
                    "<rect x='%.1f' y='%.1f' width='%.1f' height='%.1f' rx='3' fill='var(--lumo-primary-color)' opacity='%.2f'/>",
                    x, y, bw, h, 0.95 - i * 0.08));
            svg.append(String.format(java.util.Locale.ROOT,
                    "<text x='%.1f' y='%.1f' text-anchor='middle' font-size='11' fill='var(--lumo-body-text-color)' font-weight='500'>%d</text>",
                    x + bw / 2, y - 6, data[i]));
            svg.append(String.format(java.util.Locale.ROOT,
                    "<text x='%.1f' y='216' text-anchor='middle' font-size='10' fill='var(--lumo-secondary-text-color)'>%s</text>",
                    x + bw / 2, labels[i]));
        }
        svg.append("</svg>");

        Tabs metric = new Tabs(new Tab("Revenue"), new Tab("Volume"));
        metric.setSelectedIndex(0);

        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Revenue by region");
        Span sub = new Span("Top 6 markets");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);
        card.setHeaderSuffix(metric);
        card.add(new Html("<div>" + svg + "</div>"));
        return card;
    }

    private Card topCustomers() {
        int max = 0;
        for (Cust c : TOP_CUST) max = Math.max(max, c.revenue);

        VerticalLayout body = new VerticalLayout();
        body.setPadding(false);
        body.setSpacing(false);

        for (int i = 0; i < TOP_CUST.size(); i++) {
            Cust c = TOP_CUST.get(i);

            Span rank = new Span(String.valueOf(i + 1));
            rank.getStyle().set("font-size", "12px").set("color", "var(--lumo-tertiary-text-color)")
                    .set("font-variant-numeric", "tabular-nums").set("min-width", "20px").set("text-align", "center");

            Avatar av = new Avatar(c.name(), c.initials());
            av.getStyle().set("--vaadin-avatar-size", "30px");

            Span name = new Span(c.name());
            name.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.MEDIUM);

            Span amount = new Span("€" + (c.revenue / 1000) + "k");
            amount.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
            amount.getStyle().set("font-variant-numeric", "tabular-nums");

            HorizontalLayout topLine = new HorizontalLayout(rank, av, name, amount);
            topLine.setWidthFull();
            topLine.setAlignItems(FlexComponent.Alignment.CENTER);
            topLine.expand(name);

            ProgressBar bar = new ProgressBar(0, 100, (c.revenue / (double) max) * 100);

            VerticalLayout row = new VerticalLayout(topLine, bar);
            row.setPadding(false);
            row.setSpacing(false);
            row.getStyle().set("padding", "10px 0").set("gap", "8px");
            if (i < TOP_CUST.size() - 1) {
                row.getStyle().set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");
            }
            body.add(row);
        }

        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Top customers");
        Span sub = new Span("By revenue, last 30 days");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);
        card.add(body);
        return card;
    }

    private Card carrierUtilization() {
        record Carrier(String name, int pct, String kind) {}
        List<Carrier> list = List.of(
                new Carrier("DHL Express",  88, null),
                new Carrier("DPD",          72, null),
                new Carrier("GLS",          64, null),
                new Carrier("FedEx Europe", 42, "success"),
                new Carrier("UPS",          94, "warning"),
                new Carrier("Hermes",       31, "success"));

        VerticalLayout body = new VerticalLayout();
        body.setPadding(false);
        body.setSpacing(true);

        for (Carrier c : list) {
            Span name = new Span(c.name());
            name.addClassNames(LumoUtility.FontWeight.MEDIUM);
            Span pct = new Span(c.pct() + "%");
            pct.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
            pct.getStyle().set("font-variant-numeric", "tabular-nums");
            HorizontalLayout top = new HorizontalLayout(name, pct);
            top.setWidthFull();
            top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            top.setAlignItems(FlexComponent.Alignment.CENTER);

            ProgressBar pb = new ProgressBar(0, 100, c.pct());
            if (c.pct() > 85) pb.getStyle().set("--vaadin-progress-color", "var(--lumo-warning-color, #f59e0b)");
            else if ("success".equals(c.kind())) pb.getStyle().set("--vaadin-progress-color", "var(--lumo-success-color)");

            VerticalLayout row = new VerticalLayout(top, pb);
            row.setPadding(false);
            row.setSpacing(false);
            row.getStyle().set("gap", "6px");
            body.add(row);
        }

        Card card = new Card();
        card.setWidthFull();
        card.setTitle("Carrier capacity utilization");
        Span sub = new Span("Live · capacity assigned vs available");
        sub.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.setSubtitle(sub);
        card.add(body);
        return card;
    }
}
