package com.yferhaoui.reservations_ski.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public final class MainScene extends Scene {

	public MainScene(final FXMLLoader fxmlLoader) throws IOException {
		super(fxmlLoader.load());

		// load all css
		this.getStylesheets().addAll(//
				this.getClass().getClassLoader().getResource("css/fonts.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/material-color.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/skeleton.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/light.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/bootstrap.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/shape.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/typographic.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/helpers.css").toExternalForm(),
				this.getClass().getClassLoader().getResource("css/master.css").toExternalForm());
	}
}
