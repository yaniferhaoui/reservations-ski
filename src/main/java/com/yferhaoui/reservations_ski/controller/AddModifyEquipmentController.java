package com.yferhaoui.reservations_ski.controller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTextField;
import com.yferhaoui.reservations_ski.Popup;
import com.yferhaoui.reservations_ski.data.Equipment;
import com.yferhaoui.reservations_ski.data.base.Database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public final class AddModifyEquipmentController {

	@FXML
	private JFXTextField equipmentNameJFXTextField;

	// ---------------------------------------- //
	private final Database database;

	private Equipment equipment;
	private boolean validated = false;

	public AddModifyEquipmentController(final Database database) {
		this.database = database;
	}

	public AddModifyEquipmentController(final Database database, final Equipment equipment) {
		this(database);
		this.equipment = equipment;
	}

	@FXML
	public final void initialize() {
		if (this.equipment != null) {
			this.equipmentNameJFXTextField.setText(this.equipment.getName());
		}
	}

	// Getters
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

			if (StringUtils.isBlank(this.equipmentNameJFXTextField.getText())) {
				throw new Exception("Equipment Name cannot be empty !");
			}

			if (this.equipment != null) { // Update Equipment

				this.equipment.setName(this.equipmentNameJFXTextField.getText());

			} else { // New Equipment

				this.equipment = new Equipment(this.equipmentNameJFXTextField.getText());
			}

			this.database.insertOrUpdateEquipment(this.equipment);
			this.validated = true;

			// Close the window
			final Window window = ((Stage) ((Node) e.getSource()).getScene().getWindow());
			window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));

		} catch (final Exception ex) {
			Popup.alert(ex);
		}
	}
}
