package com.yferhaoui.reservations_ski.scene;

import java.io.IOException;

import com.yferhaoui.reservations_ski.controller.AddModifyEquipmentController;
import com.yferhaoui.reservations_ski.data.Equipment;
import com.yferhaoui.reservations_ski.data.base.Database;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AddModifyEquipmentWindow extends Stage {

	private final AddModifyEquipmentController controller;

	public AddModifyEquipmentWindow(final Database database, final Equipment equipment) throws IOException {
		this(database, "Modify Equipment", equipment);
	}

	public AddModifyEquipmentWindow(final Database database) throws IOException {
		this(database, "Add Equipment", null);
	}

	private AddModifyEquipmentWindow(final Database database, final String title, final Equipment equipment) throws IOException {

		this.controller = new AddModifyEquipmentController(database, equipment);
		final FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getClassLoader().getResource("fxml/AddModifyEquipment.fxml"));
		fxmlLoader.setController(this.controller);

		final Scene scene = new Scene(fxmlLoader.load());
		
		// Load all css
		scene.getStylesheets().addAll(//
				this.getClass().getClassLoader().getResource("css/fonts.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/material-color.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/skeleton.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/light.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/bootstrap.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/shape.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/typographic.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/helpers.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/master.css").toExternalForm());
		this.setScene(scene);
		this.setTitle(title);
		this.setResizable(false);
	}

	// Getter
	public final boolean isValidated() {
		return this.controller.isValidated();
	}
}
