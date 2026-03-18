package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.IntStream;

@Route(value = "data-display", layout = MainLayout.class)
@PageTitle("Data Display")
public class DataDisplayView extends VerticalLayout {

    public record Person(String name, String email, String role, int age, String status) {}

    public record Department(String name, List<Department> children) {
        public Department(String name) {
            this(name, List.of());
        }
    }

    public DataDisplayView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Data Display Components"));

        // --- Grid ---
        add(new H3("Grid"));
        var grid = new Grid<>(Person.class, false);
        grid.addColumn(Person::name).setHeader("Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(Person::email).setHeader("Email").setSortable(true).setAutoWidth(true);
        grid.addColumn(Person::role).setHeader("Role").setSortable(true).setAutoWidth(true);
        grid.addColumn(Person::age).setHeader("Age").setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(person -> {
            var badge = new Span(person.status());
            badge.getElement().getThemeList().add("badge " + switch (person.status()) {
                case "Active" -> "success";
                case "Inactive" -> "error";
                default -> "";
            });
            return badge;
        })).setHeader("Status").setAutoWidth(true);

        grid.setItems(
                new Person("Alice Johnson", "alice@example.com", "Admin", 32, "Active"),
                new Person("Bob Smith", "bob@example.com", "Developer", 28, "Active"),
                new Person("Carol White", "carol@example.com", "Designer", 35, "Inactive"),
                new Person("David Brown", "david@example.com", "Manager", 41, "Active"),
                new Person("Eve Davis", "eve@example.com", "Analyst", 26, "Inactive"),
                new Person("Frank Miller", "frank@example.com", "Developer", 30, "Active")
        );
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setHeight("300px");
        add(grid);

        add(new Hr());

        // --- Grid (compact) ---
        add(new H3("Grid (Compact)"));
        var compactGrid = new Grid<>(Person.class, false);
        compactGrid.addColumn(Person::name).setHeader("Name").setAutoWidth(true);
        compactGrid.addColumn(Person::email).setHeader("Email").setAutoWidth(true);
        compactGrid.addColumn(Person::role).setHeader("Role").setAutoWidth(true);
        compactGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER);
        compactGrid.setItems(
                new Person("Alice Johnson", "alice@example.com", "Admin", 32, "Active"),
                new Person("Bob Smith", "bob@example.com", "Developer", 28, "Active"),
                new Person("Carol White", "carol@example.com", "Designer", 35, "Inactive")
        );
        compactGrid.setHeight("200px");
        add(compactGrid);

        add(new Hr());

        // --- TreeGrid ---
        add(new H3("TreeGrid"));
        var treeGrid = new TreeGrid<>(Department.class);
        treeGrid.removeAllColumns();
        treeGrid.addHierarchyColumn(Department::name).setHeader("Department");

        var engineering = new Department("Engineering", List.of(
                new Department("Frontend"),
                new Department("Backend"),
                new Department("DevOps")
        ));
        var design = new Department("Design", List.of(
                new Department("UX"),
                new Department("Visual")
        ));
        var product = new Department("Product");

        var rootItems = List.of(engineering, design, product);
        treeGrid.setItems(rootItems, Department::children);
        treeGrid.setHeight("250px");
        add(treeGrid);

        add(new Hr());

        // --- Virtual List ---
        add(new H3("Virtual List"));
        var virtualList = new VirtualList<String>();
        virtualList.setItems(IntStream.rangeClosed(1, 100)
                .mapToObj(i -> "Virtual list item #" + i)
                .toList());
        virtualList.setRenderer(new ComponentRenderer<>(item -> {
            var layout = new HorizontalLayout();
            layout.setAlignItems(Alignment.CENTER);
            layout.add(new Icon(VaadinIcon.FILE_TEXT), new Span(item));
            return layout;
        }));
        virtualList.setHeight("200px");
        add(virtualList);

        add(new Hr());

        // --- Progress Bars ---
        add(new H3("Progress Bars"));
        var progress1 = new ProgressBar();
        progress1.setValue(0.7);

        var progress2 = new ProgressBar();
        progress2.setIndeterminate(true);

        var progress3 = new ProgressBar();
        progress3.setValue(0.3);
        progress3.getElement().getThemeList().add("error");

        var progress4 = new ProgressBar();
        progress4.setValue(0.9);
        progress4.getElement().getThemeList().add("success");

        add(new Span("70% complete"), progress1);
        add(new Span("Indeterminate"), progress2);
        add(new Span("Error variant"), progress3);
        add(new Span("Success variant"), progress4);

        add(new Hr());

        // --- Badges ---
        add(new H3("Badges"));
        var badgeRow = new HorizontalLayout();
        badgeRow.setAlignItems(Alignment.CENTER);

        for (var entry : List.of(
                new String[]{"Default", "badge"},
                new String[]{"Success", "badge success"},
                new String[]{"Error", "badge error"},
                new String[]{"Contrast", "badge contrast"},
                new String[]{"Primary", "badge primary"},
                new String[]{"Small", "badge small"},
                new String[]{"Pill", "badge pill"}
        )) {
            var badge = new Span(entry[0]);
            badge.getElement().getThemeList().add(entry[1]);
            badgeRow.add(badge);
        }
        add(badgeRow);

        add(new Hr());

        // --- Avatars ---
        add(new H3("Avatars"));
        var avatarRow = new HorizontalLayout();
        avatarRow.setAlignItems(Alignment.CENTER);

        avatarRow.add(new Avatar("Alice Johnson"));
        avatarRow.add(new Avatar("Bob Smith"));

        var xlAvatar = new Avatar("Carol White");
        xlAvatar.getElement().getThemeList().add("xlarge");
        avatarRow.add(xlAvatar);

        var avatarGroup = new AvatarGroup();
        avatarGroup.add(new AvatarGroup.AvatarGroupItem("User 1"));
        avatarGroup.add(new AvatarGroup.AvatarGroupItem("User 2"));
        avatarGroup.add(new AvatarGroup.AvatarGroupItem("User 3"));
        avatarGroup.add(new AvatarGroup.AvatarGroupItem("User 4"));
        avatarGroup.add(new AvatarGroup.AvatarGroupItem("User 5"));
        avatarGroup.setMaxItemsVisible(3);
        avatarRow.add(avatarGroup);

        add(avatarRow);
    }
}
