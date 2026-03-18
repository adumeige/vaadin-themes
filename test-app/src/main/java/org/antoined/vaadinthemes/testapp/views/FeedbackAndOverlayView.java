package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Instant;
import java.util.List;

@Route(value = "feedback-overlay", layout = MainLayout.class)
@PageTitle("Feedback & Overlay")
public class FeedbackAndOverlayView extends VerticalLayout {

    public FeedbackAndOverlayView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Feedback & Overlay Components"));

        // --- Notifications ---
        add(new H3("Notifications"));
        var notifRow = new HorizontalLayout();

        var defaultNotif = new Button("Default", e ->
                Notification.show("Default notification", 3000, Notification.Position.BOTTOM_START));

        var successNotif = new Button("Success", e -> {
            var n = Notification.show("Success notification", 3000, Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        successNotif.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        var errorNotif = new Button("Error", e -> {
            var n = Notification.show("Error notification", 3000, Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        errorNotif.addThemeVariants(ButtonVariant.LUMO_ERROR);

        var primaryNotif = new Button("Primary", e -> {
            var n = Notification.show("Primary notification", 3000, Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        });
        primaryNotif.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var contrastNotif = new Button("Contrast", e -> {
            var n = Notification.show("Contrast notification", 3000, Notification.Position.BOTTOM_START);
            n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });
        contrastNotif.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        notifRow.add(defaultNotif, successNotif, errorNotif, primaryNotif, contrastNotif);
        add(notifRow);

        add(new Hr());

        // --- Dialog ---
        add(new H3("Dialogs"));
        var dialogRow = new HorizontalLayout();

        var openDialog = new Button("Open Dialog", e -> {
            var dialog = new Dialog();
            dialog.setHeaderTitle("Sample Dialog");
            dialog.add(new VerticalLayout(
                    new Paragraph("This is a dialog with a form."),
                    new TextField("Name"),
                    new TextField("Email")
            ));
            var saveBtn = new Button("Save", ev -> dialog.close());
            saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            var cancelBtn = new Button("Cancel", ev -> dialog.close());
            dialog.getFooter().add(cancelBtn, saveBtn);
            dialog.open();
        });

        var openConfirm = new Button("Confirm Dialog", e -> {
            var confirm = new ConfirmDialog();
            confirm.setHeader("Confirm deletion");
            confirm.setText("Are you sure you want to delete this item? This action cannot be undone.");
            confirm.setCancelable(true);
            confirm.setConfirmText("Delete");
            confirm.setConfirmButtonTheme("error primary");
            confirm.open();
        });

        dialogRow.add(openDialog, openConfirm);
        add(dialogRow);

        add(new Hr());

        // --- Menu Bar ---
        add(new H3("Menu Bar"));
        var menuBar = new MenuBar();
        var fileMenu = menuBar.addItem("File");
        fileMenu.getSubMenu().addItem("New", e -> {});
        fileMenu.getSubMenu().addItem("Open", e -> {});
        fileMenu.getSubMenu().addItem("Save", e -> {});
        var editMenu = menuBar.addItem("Edit");
        editMenu.getSubMenu().addItem("Cut", e -> {});
        editMenu.getSubMenu().addItem("Copy", e -> {});
        editMenu.getSubMenu().addItem("Paste", e -> {});
        menuBar.addItem("View");
        menuBar.addItem("Help");
        add(menuBar);

        var menuBar2 = new MenuBar();
        menuBar2.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        menuBar2.addItem("Primary");
        menuBar2.addItem("Menu");
        menuBar2.addItem("Bar");
        add(menuBar2);

        var menuBar3 = new MenuBar();
        menuBar3.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        menuBar3.addItem(VaadinIcon.USER.create());
        menuBar3.addItem(VaadinIcon.COG.create());
        menuBar3.addItem(VaadinIcon.BELL.create());
        add(menuBar3);

        add(new Hr());

        // --- Accordion ---
        add(new H3("Accordion"));
        var accordion = new Accordion();
        accordion.add("Personal Information",
                new VerticalLayout(new Span("Name: Alice Johnson"), new Span("Email: alice@example.com")));
        accordion.add("Billing Address",
                new VerticalLayout(new Span("Street: 123 Main St"), new Span("City: Springfield")));
        accordion.add("Payment Method",
                new VerticalLayout(new Span("Card: **** **** **** 1234"), new Span("Expires: 12/26")));
        accordion.setWidthFull();
        add(accordion);

        add(new Hr());

        // --- Details ---
        add(new H3("Details"));
        var details1 = new Details("Contact Information",
                new VerticalLayout(new Span("Phone: +1 234 567 890"), new Span("Address: 456 Oak Ave")));
        details1.setOpened(true);

        var details2 = new Details("Additional Notes",
                new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));

        add(details1, details2);

        add(new Hr());

        // --- Message List / Message Input ---
        add(new H3("Messages"));
        var messageList = new MessageList();
        messageList.setItems(List.of(
                new MessageListItem("Hello! How can I help you today?", Instant.now().minusSeconds(300), "Support Agent"),
                new MessageListItem("I have a question about my order.", Instant.now().minusSeconds(200), "Customer"),
                new MessageListItem("Sure, I'd be happy to help! Could you share your order number?", Instant.now().minusSeconds(100), "Support Agent")
        ));
        messageList.setWidthFull();

        var messageInput = new MessageInput();
        messageInput.setWidthFull();
        messageInput.addSubmitListener(e -> {
            var items = new java.util.ArrayList<>(messageList.getItems());
            items.add(new MessageListItem(e.getValue(), Instant.now(), "You"));
            messageList.setItems(items);
        });

        add(messageList, messageInput);
    }
}
