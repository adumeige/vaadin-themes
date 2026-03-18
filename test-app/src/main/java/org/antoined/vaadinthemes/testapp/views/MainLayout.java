package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MainLayout extends AppLayout {

    public MainLayout() {
        var toggle = new DrawerToggle();
        var title = new H1("Theme Test App");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        addToNavbar(toggle, title);

        var nav = new SideNav();
        nav.addItem(new SideNavItem("Buttons & Inputs", ButtonsAndInputsView.class, VaadinIcon.EDIT.create()));
        nav.addItem(new SideNavItem("Data Display", DataDisplayView.class, VaadinIcon.TABLE.create()));
        nav.addItem(new SideNavItem("Feedback & Overlay", FeedbackAndOverlayView.class, VaadinIcon.BELL.create()));
        nav.addItem(new SideNavItem("Layout & Navigation", LayoutAndNavigationView.class, VaadinIcon.LAYOUT.create()));
        addToDrawer(nav);
    }
}
