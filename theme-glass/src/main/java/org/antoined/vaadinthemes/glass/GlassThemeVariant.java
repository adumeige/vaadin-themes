package org.antoined.vaadinthemes.glass;

import com.vaadin.flow.component.shared.ThemeVariant;

public enum GlassThemeVariant implements ThemeVariant {

    GLASSY("glass"),
    FLAT("flat");

    private final String variantName;

    GlassThemeVariant(String variantName) {
        this.variantName = variantName;
    }

    @Override
    public String getVariantName() {
        return variantName;
    }
}
