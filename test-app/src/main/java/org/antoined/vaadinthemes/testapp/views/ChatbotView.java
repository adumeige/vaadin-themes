package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Route(value = "chatbot", layout = MainLayout.class)
@PageTitle("AI Assistant")
public class ChatbotView extends VerticalLayout {

    private final MessageList messageList = new MessageList();
    private final List<MessageListItem> messages = new ArrayList<>();
    private final Random rng = new Random();

    private static final String BOT_NAME = "SYNTH-AI";
    private static final String USER_NAME = "Operator";

    private static final String[] BOT_REPLIES = {
            "Affirmative. Signal analysis complete — 3 anomalies detected in sector 7-G. Recommend manual inspection of channels CIPHER-X and HELIX-6.",
            "Running diagnostic on RF subsystem... All 7 active channels nominal. Temperature within operating range at 38.2 °C.",
            "I've cross-referenced the frequency logs. The interference pattern matches a known solar emission profile. Recommend switching to backup band.",
            "Buffer purge scheduled for 03:00 UTC. Estimated downtime: 42 seconds. Shall I notify downstream operators?",
            "Negative — that callsign is not registered in the current frequency plan. Closest match: FROST-2 on 222.100 MHz.",
            "Throughput report generated. Aggregate: 1.42 Gbps across 7 channels. Peak observed at 14:28 UTC (1.67 Gbps).",
            "Decoder pipeline is healthy. Current load: 67% CPU, 1.2 GB buffer. No packet loss in the last 300 seconds.",
            "I've updated the watchlist. ECHO-11 will be monitored on 28.410 MHz with 5-minute SNR sampling.",
            "Storage array status: 9.84 TB / 12 TB used (82%). RAID-6 with all disks reporting healthy. Consider archiving pre-Q2 captures.",
            "Authentication token refreshed. Session valid for 3600 seconds. Two-factor verification passed.",
    };

    public ChatbotView() {
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        // Header
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("gap", "var(--lumo-space-s)");

        var title = new H2("AI Assistant");

        var statusDot = new Span();
        statusDot.getStyle()
                .set("width", "8px")
                .set("height", "8px")
                .set("background", "var(--lumo-success-color)")
                .set("border-radius", "50%")
                .set("display", "inline-block")
                .set("flex-shrink", "0");

        var statusLabel = new Span("ONLINE");
        statusLabel.getStyle()
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("color", "var(--lumo-success-color)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em");

        var spacer = new Span();
        spacer.getStyle().set("flex", "1");

        var clearBtn = new Button("Clear", VaadinIcon.TRASH.create());
        clearBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        clearBtn.addClickListener(e -> {
            messages.clear();
            messageList.setItems(messages);
        });

        header.add(title, statusDot, statusLabel, spacer, clearBtn);
        add(header);

        // Bot info panel
        var infoPanel = new Div();
        infoPanel.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-left", "3px solid var(--lumo-primary-color)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-s) var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");
        infoPanel.setText("SYNTH-AI v2.4 — Signal analysis assistant. Model: GPT-SYNTH-7B. Context: Signal Console session #4821.");
        add(infoPanel);

        // Suggestion chips
        var chips = new HorizontalLayout();
        chips.getStyle().set("gap", "var(--lumo-space-xs)").set("flex-wrap", "wrap");

        for (var suggestion : List.of("Run diagnostics", "Signal report", "Check storage", "List active channels")) {
            var chip = new Button(suggestion);
            chip.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            chip.getStyle()
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("text-transform", "uppercase")
                    .set("letter-spacing", "0.08em");
            chip.addClickListener(e -> sendMessage(suggestion));
            chips.add(chip);
        }
        add(chips);

        // Seed conversation
        addBotMessage("SYNTH-AI online. Signal Console session initialized. How can I assist you, operator?");
        addUserMessage("Status report on all active channels");
        addBotMessage("All 7 active channels reporting nominal. ARCTIC-7 leads with SNR 24.3 dB. CIPHER-X showing elevated packet loss at 12.4% — recommend investigation. Full telemetry available on the Signal Dashboard.");
        addUserMessage("What about storage?");
        addBotMessage("Storage array status: 9.84 TB / 12 TB used (82%). RAID-6 with all disks healthy. At current ingest rate, estimated 14 days until capacity. Recommend archiving pre-Q2 signal captures to cold storage.");

        // Message list
        messageList.setWidthFull();
        messageList.getStyle().set("flex", "1").set("min-height", "300px");
        messageList.setItems(messages);
        add(messageList);

        // Message input
        var messageInput = new MessageInput();
        messageInput.setWidthFull();
        messageInput.addSubmitListener(e -> sendMessage(e.getValue()));
        add(messageInput);
    }

    private void sendMessage(String text) {
        addUserMessage(text);
        messageList.setItems(messages);

        getUI().ifPresent(ui -> ui.access(() -> {
            addBotMessage(BOT_REPLIES[rng.nextInt(BOT_REPLIES.length)]);
            messageList.setItems(messages);
        }));
    }

    private void addUserMessage(String text) {
        var item = new MessageListItem(text, Instant.now(), USER_NAME);
        item.setUserAbbreviation("OP");
        item.setUserColorIndex(3);
        messages.add(item);
    }

    private void addBotMessage(String text) {
        var item = new MessageListItem(text, Instant.now(), BOT_NAME);
        item.setUserAbbreviation("AI");
        item.setUserColorIndex(1);
        messages.add(item);
    }
}
