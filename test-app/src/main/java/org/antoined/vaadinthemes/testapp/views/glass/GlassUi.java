package org.antoined.vaadinthemes.testapp.views.glass;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;

/** Small helpers that build the recurring page-chrome blocks used by the
 *  three Glass demo views.
 *
 *  IMPORTANT: every helper here applies its own layout (display, padding,
 *  gap, grid, flex) via inline styles — never via theme classes. Class
 *  names are used only to hook visual styling that the theme is opinionated
 *  about (a card's border + bg, a status pill's color, etc.). This keeps
 *  the views' layout intact when the user swaps to a different theme. */
final class GlassUi {
    private GlassUi() {}

    static Div div(String className, Component... children) {
        Div d = new Div(children);
        if (className != null && !className.isEmpty()) d.setClassName(className);
        return d;
    }

    static Div div(String className, String text) {
        Div d = new Div();
        d.setText(text);
        if (className != null && !className.isEmpty()) d.setClassName(className);
        return d;
    }

    static Span span(String className, String text) {
        Span s = new Span(text);
        if (className != null && !className.isEmpty()) s.setClassName(className);
        return s;
    }

    /** Standard page outer container: full height, vertical flow. */
    static Div pageRoot() {
        Div d = new Div();
        d.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("width", "100%")
                .set("height", "100%")
                .set("min-height", "0");
        return d;
    }

    /** Top page header with title + optional sub + right-side actions.
     *  Layout (padding/flex/gap) is inlined here; the theme styles the
     *  title font via `.vg-page-title` only. */
    static Div pageHead(String title, String sub, Component... actions) {
        H1 h1 = new H1(title);
        h1.setClassName("vg-page-title");
        h1.getStyle().set("margin", "0");
        Div meta = new Div(h1);
        if (sub != null) {
            Div subDiv = new Div();
            subDiv.setText(sub);
            subDiv.setClassName("vg-page-sub");
            subDiv.getStyle().set("margin-top", "6px");
            meta.add(subDiv);
        }
        Div row = new Div(meta);
        row.getStyle()
                .set("display", "flex")
                .set("align-items", "flex-end")
                .set("justify-content", "space-between")
                .set("gap", "16px");
        if (actions.length > 0) {
            Div right = new Div(actions);
            right.getStyle().set("display", "flex").set("align-items", "center").set("gap", "8px");
            row.add(right);
        }
        Div head = new Div(row);
        head.getStyle()
                .set("padding", "24px 28px 18px")
                .set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");
        return head;
    }

    /** Page body: padded, vertical stack with gap. */
    static Div pageBody(Component... children) {
        Div body = new Div(children);
        body.getStyle()
                .set("padding", "22px 28px 28px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "24px");
        return body;
    }

    /** Card with optional title/sub and right-side action slot.
     *  The .vg-card class provides bg/border/radius/shadow only;
     *  padding inside head and body is inlined here. */
    static Div card(String title, String sub, Component action, Component... body) {
        Div card = new Div();
        card.setClassName("vg-card");
        if (title != null || action != null) {
            Div head = new Div();
            head.setClassName("vg-card-head");
            head.getStyle()
                    .set("padding", "14px 18px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "space-between")
                    .set("gap", "12px");
            Div left = new Div();
            Div t = new Div();
            t.setText(title == null ? "" : title);
            t.setClassName("vg-card-title");
            left.add(t);
            if (sub != null) {
                Div s = new Div();
                s.setText(sub);
                s.setClassName("vg-card-sub");
                s.getStyle().set("margin-top", "1px");
                left.add(s);
            }
            head.add(left);
            if (action != null) head.add(action);
            card.add(head);
        }
        Div bodyDiv = new Div(body);
        bodyDiv.getStyle().set("padding", "18px");
        card.add(bodyDiv);
        return card;
    }

    /** Card variant with the body unpadded — caller controls its own
     *  padding (used by Settings cards that contain forms/tables). */
    static Div cardBare(String title, String sub, Component... body) {
        Div card = new Div();
        card.setClassName("vg-card");
        if (title != null) {
            Div head = new Div();
            head.setClassName("vg-card-head");
            head.getStyle()
                    .set("padding", "14px 18px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "space-between")
                    .set("gap", "12px");
            Div left = new Div();
            Div t = new Div();
            t.setText(title);
            t.setClassName("vg-card-title");
            left.add(t);
            if (sub != null) {
                Div s = new Div();
                s.setText(sub);
                s.setClassName("vg-card-sub");
                s.getStyle().set("margin-top", "1px");
                left.add(s);
            }
            head.add(left);
            card.add(head);
        }
        for (Component c : body) card.add(c);
        return card;
    }

    /** Avatar disc with two-letter initials and a deterministic gradient.
     *  The .vg-customer-avatar class only sets shared visual properties
     *  (border-radius/box-shadow/color); width/height/font-size are inlined
     *  per-instance so callers can pick a size. */
    static Div avatar(String initials, int hue, int sizePx) {
        Div a = new Div();
        a.setText(initials);
        a.setClassName("vg-customer-avatar");
        a.getStyle()
                .set("width", sizePx + "px")
                .set("height", sizePx + "px")
                .set("font-size", Math.max(10, sizePx / 3) + "px")
                .set("background",
                        "linear-gradient(135deg, hsl(" + hue + " 65% 55%), hsl(" + ((hue + 30) % 360) + " 65% 45%))");
        return a;
    }

    /** Status pill — kind ∈ {in-transit, delivered, delayed, cancelled, pending}. */
    static Span status(String label, String kind) {
        Span s = new Span(label);
        s.setClassName("vg-status vg-status-" + kind);
        return s;
    }

    /** Segmented control (pill-style tabs — different from vaadin-tabs). */
    static Div segmentedTabs(String[] labels, int selectedIndex, java.util.function.IntConsumer onSelect) {
        Div bar = new Div();
        bar.setClassName("vg-tabs");
        for (int i = 0; i < labels.length; i++) {
            int idx = i;
            Div t = new Div();
            t.setText(labels[i]);
            t.setClassName("vg-tab" + (i == selectedIndex ? " is-active" : ""));
            t.getElement().addEventListener("click", e -> onSelect.accept(idx));
            bar.add(t);
        }
        return bar;
    }

    /** Section block: title row + grid of fields. Layout (padding,
     *  border-bottom, gap) inlined; visual styling on the headings
     *  comes from .vg-form-section-title / -sub classes. */
    static Div formSection(String title, String sub, String side, Component... fields) {
        Div section = new Div();
        section.getStyle()
                .set("padding", "22px 24px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "14px")
                .set("border-bottom", "0.5px solid var(--lumo-contrast-10pct)");

        Div head = new Div();
        head.getStyle()
                .set("display", "flex")
                .set("align-items", "baseline")
                .set("justify-content", "space-between")
                .set("gap", "16px");
        Div meta = new Div();
        meta.add(span("vg-form-section-title", title));
        if (sub != null) {
            Div s = new Div();
            s.setText(sub);
            s.setClassName("vg-form-section-sub");
            s.getStyle().set("margin-top", "2px");
            meta.add(s);
        }
        head.add(meta);
        if (side != null) {
            Div s = new Div();
            s.setText(side);
            s.setClassName("vg-form-section-side");
            head.add(s);
        }

        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(2, minmax(0, 1fr))")
                .set("gap", "14px 16px");
        for (Component f : fields) grid.add(f);

        section.add(head, grid);
        return section;
    }

    static Html svg(String inner) {
        return new Html("<div>" + inner + "</div>");
    }

    /** Convenience to add many children to a HasComponents. */
    static <T extends HasComponents> T addAll(T parent, Component... children) {
        for (Component c : children) parent.add(c);
        return parent;
    }

    /** Apply a setup function to a component and return it — lets us inline
     *  style/config without breaking the flow of div(...).add(...) chains. */
    static <T extends Component> T tap(T c, java.util.function.Consumer<T> setup) {
        setup.accept(c);
        return c;
    }
}
