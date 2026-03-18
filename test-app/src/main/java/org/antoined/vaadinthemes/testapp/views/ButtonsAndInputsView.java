package org.antoined.vaadinthemes.testapp.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "buttons-inputs", layout = MainLayout.class)
@PageTitle("Buttons & Inputs")
public class ButtonsAndInputsView extends VerticalLayout {

    public ButtonsAndInputsView() {
        setPadding(true);
        setSpacing(true);

        add(new H2("Buttons & Input Components"));

        // --- Buttons ---
        add(new H3("Buttons"));
        var btnRow1 = new HorizontalLayout();
        btnRow1.setAlignItems(Alignment.BASELINE);

        var primary = new Button("Primary");
        primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var secondary = new Button("Secondary");

        var tertiary = new Button("Tertiary");
        tertiary.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var success = new Button("Success");
        success.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        var error = new Button("Error");
        error.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        var contrast = new Button("Contrast");
        contrast.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        var disabled = new Button("Disabled");
        disabled.setEnabled(false);

        var iconBtn = new Button("With Icon", VaadinIcon.PLUS.create());
        iconBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var small = new Button("Small");
        small.addThemeVariants(ButtonVariant.LUMO_SMALL);

        var large = new Button("Large");
        large.addThemeVariants(ButtonVariant.LUMO_LARGE);

        btnRow1.add(primary, secondary, tertiary, success, error, contrast, disabled, iconBtn, small, large);
        add(btnRow1);

        // Tertiary variants
        var btnRow2 = new HorizontalLayout();
        btnRow2.setAlignItems(Alignment.BASELINE);

        var tertiarySuccess = new Button("Tertiary Success");
        tertiarySuccess.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

        var tertiaryError = new Button("Tertiary Error");
        tertiaryError.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        var iconOnly = new Button(VaadinIcon.COG.create());
        iconOnly.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        btnRow2.add(tertiarySuccess, tertiaryError, iconOnly);
        add(btnRow2);

        add(new Hr());

        // --- Text Fields ---
        add(new H3("Text Fields"));
        var textRow = new HorizontalLayout();
        textRow.setWidthFull();
        textRow.setFlexGrow(1);

        var textField = new TextField("Text Field");
        textField.setPlaceholder("Enter text...");
        textField.setHelperText("Helper text here");

        var emailField = new EmailField("Email");
        emailField.setPlaceholder("user@example.com");
        emailField.setClearButtonVisible(true);

        var passwordField = new PasswordField("Password");
        passwordField.setHelperText("At least 8 characters");

        var numberField = new NumberField("Number");
        numberField.setStepButtonsVisible(true);
        numberField.setMin(0);
        numberField.setMax(100);

        var intField = new IntegerField("Integer");
        intField.setStepButtonsVisible(true);

        var bigDecField = new BigDecimalField("Big Decimal");

        textRow.add(textField, emailField, passwordField, numberField, intField, bigDecField);
        add(textRow);

        var textArea = new TextArea("Text Area");
        textArea.setPlaceholder("Multi-line text...");
        textArea.setWidthFull();
        textArea.setMaxLength(500);
        textArea.setHelperText("0/500");
        textArea.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);
        textArea.addValueChangeListener(e ->
                textArea.setHelperText(e.getValue().length() + "/500"));

        var invalidField = new TextField("Invalid Field");
        invalidField.setInvalid(true);
        invalidField.setErrorMessage("This field has an error");

        var readonlyField = new TextField("Read-only");
        readonlyField.setValue("Cannot edit this");
        readonlyField.setReadOnly(true);

        add(new HorizontalLayout(textArea, invalidField, readonlyField) {{
            setWidthFull();
        }});

        add(new Hr());

        // --- Date/Time Pickers ---
        add(new H3("Date & Time Pickers"));
        var dateRow = new HorizontalLayout();

        var datePicker = new DatePicker("Date Picker");
        datePicker.setPlaceholder("Pick a date");
        datePicker.setClearButtonVisible(true);

        var timePicker = new TimePicker("Time Picker");
        timePicker.setClearButtonVisible(true);

        var dateTimePicker = new DateTimePicker("Date Time Picker");

        dateRow.add(datePicker, timePicker, dateTimePicker);
        add(dateRow);

        add(new Hr());

        // --- Selection Components ---
        add(new H3("Selection Components"));

        var selRow1 = new HorizontalLayout();
        selRow1.setAlignItems(Alignment.BASELINE);

        var comboBox = new ComboBox<String>("ComboBox");
        comboBox.setItems("Option A", "Option B", "Option C", "Option D");
        comboBox.setPlaceholder("Select...");
        comboBox.setClearButtonVisible(true);

        var multiCombo = new MultiSelectComboBox<String>("Multi-Select ComboBox");
        multiCombo.setItems("Red", "Green", "Blue", "Yellow", "Purple");
        multiCombo.setPlaceholder("Pick colors...");

        var select = new Select<String>();
        select.setLabel("Select");
        select.setItems("First", "Second", "Third");
        select.setPlaceholder("Choose...");

        selRow1.add(comboBox, multiCombo, select);
        add(selRow1);

        var selRow2 = new HorizontalLayout();
        selRow2.setAlignItems(Alignment.START);

        var radioGroup = new RadioButtonGroup<String>();
        radioGroup.setLabel("Radio Buttons");
        radioGroup.setItems("Radio 1", "Radio 2", "Radio 3");
        radioGroup.setValue("Radio 1");

        var checkboxGroup = new CheckboxGroup<String>();
        checkboxGroup.setLabel("Checkbox Group");
        checkboxGroup.setItems("Check A", "Check B", "Check C");

        var checkbox = new Checkbox("Standalone Checkbox");
        checkbox.setValue(true);

        selRow2.add(radioGroup, checkboxGroup, checkbox);
        add(selRow2);

        var selRow3 = new HorizontalLayout();
        selRow3.setAlignItems(Alignment.START);

        var listBox = new ListBox<String>();
        listBox.setItems("Item 1", "Item 2", "Item 3", "Item 4");

        var multiListBox = new MultiSelectListBox<String>();
        multiListBox.setItems("Alpha", "Beta", "Gamma", "Delta");

        selRow3.add(listBox, multiListBox);
        add(selRow3);

        add(new Hr());

        // --- Upload ---
        add(new H3("Upload"));
        var upload = new Upload(new MemoryBuffer());
        upload.setMaxFiles(3);
        upload.setAcceptedFileTypes("image/*", ".pdf");
        add(upload);
    }
}
