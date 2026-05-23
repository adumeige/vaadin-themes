package org.antoined.vaadinthemes.theme;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.shared.ThemeVariant;
import com.vaadin.flow.dom.ThemeList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public final class ThemeVariants {

    private ThemeVariants() {
    }

    public static void add(Component component, ThemeVariant... variants) {
        ThemeList themeList = themeList(component);
        Arrays.stream(variants)
                .filter(Objects::nonNull)
                .map(ThemeVariant::getVariantName)
                .forEach(themeList::add);
    }

    public static void remove(Component component, ThemeVariant... variants) {
        ThemeList themeList = themeList(component);
        Arrays.stream(variants)
                .filter(Objects::nonNull)
                .map(ThemeVariant::getVariantName)
                .forEach(themeList::remove);
    }

    public static void replace(Component component, ThemeVariant variant, Collection<? extends ThemeVariant> family) {
        ThemeList themeList = themeList(component);
        family.stream()
                .filter(Objects::nonNull)
                .map(ThemeVariant::getVariantName)
                .forEach(themeList::remove);
        if (variant != null) {
            themeList.add(variant.getVariantName());
        }
    }

    public static <TVariant extends Enum<TVariant> & ThemeVariant> void replace(
            Component component,
            TVariant variant,
            Class<TVariant> family
    ) {
        replace(component, variant, Arrays.asList(family.getEnumConstants()));
    }

    public static boolean contains(Component component, ThemeVariant variant) {
        return variant != null && themeList(component).contains(variant.getVariantName());
    }

    private static ThemeList themeList(Component component) {
        Objects.requireNonNull(component, "component must not be null");
        return component.getElement().getThemeList();
    }
}
