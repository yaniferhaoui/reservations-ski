package com.yferhaoui.reservations_ski.scene;

import java.io.IOException;

import com.yferhaoui.reservations_ski.controller.AddModifyPersonController;
import com.yferhaoui.reservations_ski.data.Person;
import com.yferhaoui.reservations_ski.data.base.Database;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;



public final class AddModifyPersonWindow extends Stage {

	private final AddModifyPersonController controller;

	public AddModifyPersonWindow(final Database database, final Person person) throws IOException {
		this(database, "Modify Person", person);
	}

	public AddModifyPersonWindow(final Database database) throws IOException {
		this(database, "Add Person", null);
	}

	private AddModifyPersonWindow(final Database database, final String title, final Person person) throws IOException {

		this.controller = new AddModifyPersonController(database, person);
		final FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getClassLoader().getResource("fxml/AddModifyPerson.fxml"));
		fxmlLoader.setController(this.controller);

		final Scene scene = new Scene(fxmlLoader.load());
		
		// load all css
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
