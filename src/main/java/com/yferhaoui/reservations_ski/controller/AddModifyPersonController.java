package com.yferhaoui.reservations_ski.controller;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.yferhaoui.reservations_ski.Popup;
import com.yferhaoui.reservations_ski.data.Gender;
import com.yferhaoui.reservations_ski.data.Level;
import com.yferhaoui.reservations_ski.data.Person;
import com.yferhaoui.reservations_ski.data.base.Database;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public final class AddModifyPersonController {

	@FXML
	private JFXTextField firstNameTextField;

	@FXML
	private JFXTextField lastNameTextField;

	@FXML
	private RadioButton femmeRadioButton;

	@FXML
	private RadioButton hommeRadioButton;

	@FXML
	private JFXTextField weightTextField;

	@FXML
	private JFXTextField heightTextField;

	@FXML
	private JFXComboBox<Level> levelComboBox;

	@FXML
	private DatePicker birthDateDatePicker;

	// ----------------------- //
	private final Database database;

	private Person person;
	private boolean validated = false;

	public AddModifyPersonController(final Database database) {
		this.database = database;
	}

	public AddModifyPersonController(final Database database, final Person person) {
		this(database);
		this.person = person;
	}

	@FXML
	public final void initialize() {

		// Weight JFXTextField (Only Numbers)
		this.weightTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public final void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				if (!newValue.matches("\\d{0,3}([\\.]\\d{0,1})?")) {
					AddModifyPersonController.this.weightTextField.setText(oldValue);
				}
			}
		});

		// Height JFXTextField (Only Numbers)
		this.heightTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public final void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				if (!newValue.matches("\\d{0,3}([\\.]\\d{0,1})?")) {
					AddModifyPersonController.this.heightTextField.setText(oldValue);
				}
			}
		});

		// Level JFXComboBox
		this.levelComboBox.getItems().addAll(Level.values());

		// If mode modify -> fill fields
		if (this.person != null) {
			this.firstNameTextField.setText(this.person.getFirstName());
			this.lastNameTextField.setText(this.person.getLastName());

			if (this.person.getGender().equals(Gender.FEMALE)) {
				this.femmeRadioButton.setSelected(true);
			} else if (this.person.getGender().equals(Gender.MALE)) {
				this.hommeRadioButton.setSelected(true);
			}

			this.weightTextField.setText(String.valueOf(this.person.getWeight()));
			this.heightTextField.setText(String.valueOf(this.person.getHeight()));
			this.levelComboBox.setValue(this.person.getLevel());

			final LocalDate localDate = Instant.ofEpochMilli(this.person.getBirthDate().getTime())//
					.atZone(ZoneId.systemDefault())//
					.toLocalDate();
			this.birthDateDatePicker.setValue(localDate);
		}
	}

	// Getter
	public final boolean isValidated() {
		return this.validated;
	}

	// Action Events
	// When the button Cancel is performed
	@FXML
	public final void cancel(final ActionEvent e) throws IOException, InterruptedException {
		final Stage window = ((Stage) ((Node) e.getSource()).getScene().getWindow());
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	// When the button Validate is performed
	@FXML
	public final void validate(final ActionEvent e) throws IOException, InterruptedException {
		try {

			if (StringUtils.isBlank(this.firstNameTextField.getText())) {
				throw new Exception("First Name cannot be empty !");
			}

			if (StringUtils.isBlank(this.lastNameTextField.getText())) {
				throw new Exception("Last Name cannot be null !");
			}

			if (!this.femmeRadioButton.isSelected() && !this.hommeRadioButton.isSelected()) {
				throw new Exception("Please select a gender !");
			}

			if (StringUtils.isBlank(this.weightTextField.getText())) {
				throw new Exception("Weight cannot be null !");
			}

			if (StringUtils.isBlank(this.heightTextField.getText())) {
				throw new Exception("Height cannot be null !");
			}

			if (this.levelComboBox.getValue() == null) {
				throw new Exception("Level cannot be null !");
			}

			if (this.birthDateDatePicker.getValue() == null) {
				throw new Exception("Birth Date cannot be empty !");
			}

			final LocalDate localDate = this.birthDateDatePicker.getValue();
			
			if (localDate.isAfter(LocalDate.now())) {
				throw new Exception("Birth Date must be before current date !");
			}
			
			if (this.person != null) { // Update the person

				this.person.setFirstName(this.firstNameTextField.getText());
				this.person.setLastName(this.lastNameTextField.getText());
				this.person.setGender(this.femmeRadioButton.isSelected() ? Gender.FEMALE : Gender.MALE);
				this.person.setWeight(Integer.valueOf(this.weightTextField.getText()));
				this.person.setHeight(Integer.valueOf(this.heightTextField.getText()));
				this.person.setLevel(this.levelComboBox.getValue());
				this.person.setBirthDate(Date.valueOf(localDate));

			} else { // Create new person

				this.person = new Person(this.firstNameTextField.getText(), //
						this.lastNameTextField.getText(), //
						this.femmeRadioButton.isSelected() ? Gender.FEMALE : Gender.MALE, //
						Integer.valueOf(this.weightTextField.getText()), //
						Integer.valueOf(this.heightTextField.getText()), //
						this.levelComboBox.getValue(), //
						localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
			}

			this.database.insertOrUpdatePerson(this.person);
			this.validated = true;

			// Close the current window
			final Window window = ((Stage) ((Node) e.getSource()).getScene().getWindow());
			window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));

		} catch (final Exception ex) {
			Popup.alert(ex);
		}
	}
}
